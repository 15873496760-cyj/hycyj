package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;


	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//向第一个表添加数据
		specificationMapper.insert(specification.getSpec());
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			//将id于TbSpecificationOption的外检进行绑定
			option.setSpecId(specification.getSpec().getId());
			//将数据加入数据库
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//修改表数据
		specificationMapper.updateByPrimaryKey(specification.getSpec());
		//创建条件对象
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		//添加条件
		criteria.andSpecIdEqualTo(specification.getSpec().getId());
		//先进行删除
		specificationOptionMapper.deleteByExample(example);
		//添加数据
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			//将id于其绑定
			option.setSpecId(specification.getSpec().getId());
			//添加数据
			specificationOptionMapper.insert(option);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//找到规格对象
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		//根据外键在tb_specification这个表中查询出此规格对应的规格选项列表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		//查询出这个外键关联的规格选项列表
		List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
		//构造Specification组合对象
		Specification specification = new Specification();
		specification.setSpec(tbSpecification);
		specification.setSpecificationOptionList(tbSpecificationOptions);
		//返回组合对象
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 查询所有信息（List<Map>(数据格式)）
	 * @return
	 */
	@Override
	public List<Map> findSpecList() {
		return specificationMapper.findSpecList();
	}

}
