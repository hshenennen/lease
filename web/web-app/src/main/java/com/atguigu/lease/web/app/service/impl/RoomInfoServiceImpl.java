package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
		implements RoomInfoService {

	@Autowired
	private RoomInfoMapper roomInfoMapper;//房间

	@Autowired
	private ApartmentInfoService apartmentInfoService;//公寓

	@Autowired
	private GraphInfoMapper graphInfoMapper;//图片

	@Autowired
	private AttrValueMapper attrValueMapper;//属性信息

	@Autowired
	private FacilityInfoMapper facilityInfoMapper;//配套信息

	@Autowired
	private LabelInfoMapper labelInfoMapper;//标签信息

	@Autowired
	private PaymentTypeMapper paymentTypeMapper;//支付方式

	@Autowired
	private FeeValueMapper feeValueMapper;//杂费

	@Autowired
	private LeaseTermMapper leaseTermMapper;//租期

	@Autowired
	private BrowsingHistoryService browsingHistoryService;//浏览历史

	//分页查询房间列表
	@Override
	public IPage<RoomItemVo> pageRoomItemVo(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
		return roomInfoMapper.pageRoomItemVo(page, queryVo);
	}

	//根据id获取房间的详细信息
	@Override
	@Transactional
	public RoomDetailVo getDetailById(Long id) {
		RoomInfo roomInfo = roomInfoMapper.selectById(id);//房间基本信息
		if (roomInfo == null) {
			return null;
		}
		Long roomInfoId = roomInfo.getId();//房间id
		Long apartmentId = roomInfo.getApartmentId();//公寓id
		//公寓信息
		ApartmentItemVo apartmentItemVo = apartmentInfoService.getApartmentItemVoByroomId(apartmentId);

		//图片列表
		List<GraphVo> graphVoList = graphInfoMapper.getgraphVoList(ItemType.ROOM, roomInfoId);

		//属性信息列表
		List<AttrValueVo> attrValueVoList = attrValueMapper.getattrValueVoListByRoomId(roomInfoId);

		//配套信息列表
		List<FacilityInfo> facilityInfoList = facilityInfoMapper.getFacilityInfoListByRoomId(roomInfoId);

		//标签信息列表
		List<LabelInfo> labelInfoList = labelInfoMapper.getLabelInfoByRoomtId(roomInfoId);

		//支付方式列表
		List<PaymentType> paymentTypeList = paymentTypeMapper.getPaymentTypeListByRoomId(roomInfoId);

		//杂费列表
		List<FeeValueVo> feeValueVoList = feeValueMapper.getFeeValueVoListByApartmentId(apartmentId);

		//租期列表
		List<LeaseTerm> leaseTermList = leaseTermMapper.getleaseTermListByRoomId(roomInfoId);


		//封装返回结果
		RoomDetailVo roomDetailVo = new RoomDetailVo();
		BeanUtils.copyProperties(roomInfo, roomDetailVo);
		roomDetailVo.setApartmentItemVo(apartmentItemVo);
		roomDetailVo.setGraphVoList(graphVoList);
		roomDetailVo.setAttrValueVoList(attrValueVoList);
		roomDetailVo.setFacilityInfoList(facilityInfoList);
		roomDetailVo.setLabelInfoList(labelInfoList);
		roomDetailVo.setPaymentTypeList(paymentTypeList);
		roomDetailVo.setFeeValueVoList(feeValueVoList);
		roomDetailVo.setLeaseTermList(leaseTermList);

		//保存浏览历史
		browsingHistoryService.saveHistory(LoginUserHolder.getLoginUser().getUserId(), roomInfoId);


		return roomDetailVo;
	}

	//根据公寓id分页查询房间列表
	@Override
	public IPage<RoomItemVo> pageRoomItemVoByApartmentId(IPage<RoomItemVo> page, Long id) {
		return roomInfoMapper.pageRoomItemVoByApartmentId(page, id);
	}
}




