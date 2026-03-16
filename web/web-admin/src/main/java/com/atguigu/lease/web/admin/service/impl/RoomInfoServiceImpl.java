package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
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
	private RoomAttrValueService roomAttrValueService;//属性_和房间中间关系表-Service

	@Autowired
	private RoomFacilityService roomFacilityService;//配套_和房间中间关系表-Service

	@Autowired
	private RoomLabelService roomLabelService;//标签_和房间中间关系表-Service

	@Autowired
	private RoomPaymentTypeService roomPaymentTypeService;//支付方式_和房间中间关系表-Service

	@Autowired
	private RoomLeaseTermService roomLeaseTermService;//可选租期_和房间中间关系表-Service

	@Autowired
	private RoomInfoMapper roomInfoMapper;//房间-Mapper

	@Autowired
	private ApartmentInfoMapper apartmentInfoMapper;//公寓-Mapper

	@Autowired
	private GraphInfoMapper graphInfoMapper;//图片-Mapper

	@Autowired
	private AttrValueMapper attrValueMapper;//属性-Mapper

	@Autowired
	private FacilityInfoMapper facilityInfoMapper;//配套-Mapper

	@Autowired
	private LabelInfoMapper labelInfoMapper;//标签-Mapper

	@Autowired
	private PaymentTypeMapper paymentTypeMapper;//支付方式-Mapper

	@Autowired
	private LeaseTermMapper leaseTermMapper;//可选租期-Mapper

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
				RoomLabel roomLabel = RoomLabel.builder()
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

	//根据条件分页查询房间列表
	@Override
	public IPage<RoomItemVo> pageRoomItemByQuery(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
		return roomInfoMapper.pageRoomItemByQuery(page, queryVo);
	}

	//根据id获取房间详细信息
	@Override
	@Transactional
	public RoomDetailVo getRoomDetailVoByRoomId(Long id) {
		//1.获取房间信息
		RoomInfo roomInfo = roomInfoMapper.selectById(id);
		//异常处理
		if (roomInfo == null) {
			throw new RuntimeException();
		}
		//2.获取公寓信息
		ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());
		//3.获取图片
		List<GraphVo> graphVoList = graphInfoMapper.getGraphInfoByItemTypeAndId(ItemType.ROOM, id);
		//4.获取属性信息
		List<AttrValueVo> attrValueVoList = attrValueMapper.selectListByRoomId(id);
		//5.获取配套信息
		List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);
		//6.获取标签信息
		List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);
		//7.获取支付方式
		List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);
		//8.获取可选租期
		List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);

		//封装返回结果
		RoomDetailVo roomDetailVo = new RoomDetailVo();
		BeanUtils.copyProperties(roomInfo, roomDetailVo);//房间基本信息

		roomDetailVo.setApartmentInfo(apartmentInfo);//公寓信息
		roomDetailVo.setGraphVoList(graphVoList);//图片
		roomDetailVo.setAttrValueVoList(attrValueVoList);//属性信息
		roomDetailVo.setFacilityInfoList(facilityInfoList);//配套信息
		roomDetailVo.setLabelInfoList(labelInfoList);//标签信息
		roomDetailVo.setPaymentTypeList(paymentTypeList);//支付方式
		roomDetailVo.setLeaseTermList(leaseTermList);//可选租期

		return roomDetailVo;
	}
}




