package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.BrowsingHistory;
import com.atguigu.lease.web.app.mapper.BrowsingHistoryMapper;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
		implements BrowsingHistoryService {

	@Autowired
	private BrowsingHistoryMapper browsingHistoryMapper;

	@Override
	public IPage<HistoryItemVo> pageHistoryItemVoByUserId(IPage<HistoryItemVo> page, Long userId) {
		return browsingHistoryMapper.pageHistoryItemVoByUserId(page, userId);
	}

	//保存浏览历史
	@Override
	@Async //进行异步处理
	public void saveHistory(Long userId, Long roomInfoId) {
		//通过用户id和房间id，来查询浏览历史有没有记录
		LambdaQueryWrapper<BrowsingHistory> queryWrapper = new LambdaQueryWrapper<BrowsingHistory>()
				.eq(BrowsingHistory::getUserId, userId)
				.eq(BrowsingHistory::getRoomId, roomInfoId);
		BrowsingHistory browsingHistory = browsingHistoryMapper.selectOne(queryWrapper);
		//一个用户，同一个房间的浏览历史，就存一条到数据库中
		//有浏览历史，就更新修改时间，没有就新增一个浏览历史
		if (browsingHistory != null) {
			browsingHistory.setBrowseTime(new Date());
			browsingHistoryMapper.updateById(browsingHistory);
		} else {
			BrowsingHistory history = new BrowsingHistory();
			history.setUserId(userId);
			history.setRoomId(roomInfoId);
			history.setBrowseTime(new Date());
			browsingHistoryMapper.insert(history);
		}
	}
}