package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.GraphInfoMapper;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
		implements RoomInfoService {

	@Autowired
	private GraphInfoService graphInfoService;//图片-Service

	@Autowired
	private RoomAttrValueService roomAttrValueService;//属性-Service

	@Autowired
	private RoomFacilityService roomFacilityService;//配套-Service

	@Autowired
	private RoomLabelService roomLabelService;//标签-Service

	@Autowired
	private RoomPaymentTypeService roomPaymentTypeService;//支付方式-Service

	@Autowired
	private RoomLeaseTermService roomLeaseTermService;//可选租期-Service

	//保存或更新房间信息
	@Override
	@Transactional//事件管理
	public void saveOrUpdateRoomSubmitVo(RoomSubmitVo roomSubmitVo) {
		//先判断是新增还是修改
		Boolean isUpdate = roomSubmitVo.getId() != null;
		if (isUpdate) {//有id，先删除，再添加
			//1.删除图片
			LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<GraphInfo>()
					.eq(GraphInfo::getItemType, ItemType.ROOM)
					.eq(GraphInfo::getItemId, roomSubmitVo.getId());
			graphInfoService.remove(graphInfoLambdaQueryWrapper);
			//2.删除属性
			LambdaQueryWrapper<RoomAttrValue> attrValueLambdaQueryWrapper = new LambdaQueryWrapper<RoomAttrValue>()
					.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
			roomAttrValueService.remove(attrValueLambdaQueryWrapper);
			//3.删除配套
			LambdaQueryWrapper<RoomFacility> roomFacilityLambdaQueryWrapper = new LambdaQueryWrapper<RoomFacility>()
					.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
			roomFacilityService.remove(roomFacilityLambdaQueryWrapper);
			//4.删除标签
			LambdaQueryWrapper<RoomLabel> roomLabelLambdaQueryWrapper = new LambdaQueryWrapper<RoomLabel>()
					.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
			roomLabelService.remove(roomLabelLambdaQueryWrapper);
			//5.删除支付方式
			LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeLambdaQueryWrapper = new LambdaQueryWrapper<RoomPaymentType>()
					.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
			roomPaymentTypeService.remove(roomPaymentTypeLambdaQueryWrapper);

			//6.删除可选租期
			LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermLambdaQueryWrapper = new LambdaQueryWrapper<RoomLeaseTerm>()
					.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
			roomLeaseTermService.remove(roomLeaseTermLambdaQueryWrapper);
		}
		//添加(重新)内容
		//1.添加图片
		List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
		List<GraphInfo> graphInfoList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(graphVoList)) {//非空判断
			for (GraphVo graphVo : graphVoList) {
				//补全信息
				GraphInfo graphInfo = GraphInfo.builder()
						.name(graphVo.getName())
						.itemType(ItemType.ROOM)
						.itemId(roomSubmitVo.getId())
						.url(graphVo.getUrl())
						.build();
				//添加到集合中
				graphInfoList.add(graphInfo);
			}
			graphInfoService.saveBatch(graphInfoList);
		}

		//2.添加属性
		List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
		List<RoomAttrValue> roomAttrValueList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(attrValueIds)) {
			for (Long attrValueId : attrValueIds) {
				RoomAttrValue roomAttrValue = RoomAttrValue.builder()
						.attrValueId(attrValueId)
						.roomId(roomSubmitVo.getId())
						.build();
				roomAttrValueList.add(roomAttrValue);
			}
			roomAttrValueService.saveBatch(roomAttrValueList);
		}

		//3.添加配套
		List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
		List<RoomFacility> roomFacilityList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(facilityInfoIds)) {
			for (Long facilityInfoId : facilityInfoIds) {
				RoomFacility roomFacility = RoomFacility.builder()
						.facilityId(facilityInfoId)
						.roomId(roomSubmitVo.getId())
						.build();
				roomFacilityList.add(roomFacility);
			}
			roomFacilityService.saveBatch(roomFacilityList);
		}

		//4.增加标签
		List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
		List<RoomLabel> roomLabelList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(labelInfoIds)) {
			for (Long labelInfoId : labelInfoIds) {
				RoomLabel roomLabel =  RoomLabel.builder()
						.labelId(labelInfoId)
						.roomId(roomSubmitVo.getId())
						.build();
				roomLabelList.add(roomLabel);
			}
			roomLabelService.saveBatch(roomLabelList);
		}

		//5.增加支付方式
		List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
		List<RoomPaymentType> roomPaymentTypeArrayList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(paymentTypeIds)) {
			for (Long paymentTypeId : paymentTypeIds) {
				RoomPaymentType roomPaymentType = RoomPaymentType.builder()
						.roomId(roomSubmitVo.getId())
						.paymentTypeId(paymentTypeId)
						.build();
				roomPaymentTypeArrayList.add(roomPaymentType);
			}
			roomPaymentTypeService.saveBatch(roomPaymentTypeArrayList);
		}

		//6.增加可选租期
		List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
		List<RoomLeaseTerm> roomLeaseTermList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(leaseTermIds)) {
			for (Long leaseTermId : leaseTermIds) {
				RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder()
						.roomId(roomSubmitVo.getId())
						.leaseTermId(leaseTermId)
						.build();
				roomLeaseTermList.add(roomLeaseTerm);
			}
			roomLeaseTermService.saveBatch(roomLeaseTermList);
		}

	}
}




