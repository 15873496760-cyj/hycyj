package com.pinyougou.sellergoods.service;
import java.util.List;

import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbSeller;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SellerService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeller> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeller seller);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeller seller);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeller findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeller seller, int pageNum, int pageSize);

	/**
	 * 修改状态
	 * @param sellerId
	 * @param status
	 */
	void updateStatus(String sellerId, String status);

	/**
	 * 根据id查询
	 * @param sellerId
	 * @return
	 */
	TbSeller selectByPrimaryKey(String sellerId);
}
