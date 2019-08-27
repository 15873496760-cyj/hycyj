package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		List<TbGoods> tbGoods = goodsMapper.selectByExample(null);
		List<TbGoods> goods = new ArrayList<>();
		for (TbGoods tbGood : tbGoods) {
			if (tbGood.getAuditStatus().equals("0") && !tbGood.getIsDelete().equals("1")) {
				goods.add(tbGood);
			}
		}
		return goods;
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		List<TbGoods> tbGoods = goodsMapper.selectByExample(null);
		PageHelper.startPage(pageNum, pageSize);
		List<TbGoods> goods = new ArrayList<>();
		for (TbGoods tbGood : tbGoods) {
			if (tbGood.getAuditStatus().equals("0") && !tbGood.getIsDelete().equals("1")) {
				goods.add(tbGood);
			}
		}
		Page<TbGoods> page=   (Page<TbGoods>) goods;
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//添加到tb_goods表中
		goodsMapper.insert(goods.getGoods());
		//添加到tb_goods_desc表中
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		//添加到tb_item表中
		//3.向tb_item表添加记录
		insertItems(goods);
	}

	private void insertItems(Goods goods) {
		for (TbItem item : goods.getItems()) {
			item.setTitle(goods.getGoods().getGoodsName());
			//得到品牌名字
			String brandName = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId()).getName();
			//设置品牌名字
			item.setBrand(brandName);
			//得到三级分类id
			Long category3Id = goods.getGoods().getCategory3Id();
			item.setCategoryid(category3Id);
			//根据三级分类查询分类名称
			String categoryName = itemCatMapper.selectByPrimaryKey(category3Id).getName();
			item.setCategory(categoryName);
			item.setSeller(goods.getGoods().getSellerId());
			item.setGoodsId(goods.getGoods().getId());
			item.setUpdateTime(new Date());
			//设置图片，就是得到 goodsDesc表中的imageItems这个数组中的第一张图片
			String itemImages = goods.getGoodsDesc().getItemImages();
			//转换图片json对象为java对象
			List<Map> imageMap = JSON.parseArray(itemImages,Map.class);
			//判断是否有值
			if(imageMap != null && imageMap.size() > 0){
				String url = (String) imageMap.get(0).get("url");
				item.setImage(url);

			}
			item.setCreateTime(new Date());
			//保存sku商品
			itemMapper.insert(item);
		}
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//先删除在添加
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		insertItems(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		//通过id得到tbgoods的对象值
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		//获取goodsdesc的值
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		//将查出的数据与goods绑定
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);

		//查询出items的数据值并且进行绑定
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItems(tbItems);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			//根据id查询出商品对象
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");		//执行逻辑删除操作
			//执行修改操作
			goodsMapper.updateByPrimaryKey(goods);
		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	//商品审核
	@Override
	public void updateStatus(String[] ids, String status) {
		for (String id : ids) {
			//1.根据商品id查询出商品对象
			TbGoods goods = goodsMapper.selectByPrimaryKey(Long.parseLong(id));
			//2.修改商品的状态值
			goods.setAuditStatus(status);
			//3.审核商品
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

	
}
