package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
		implements ApartmentInfoService {

	@Autowired
	private GraphInfoService graphInfoService;//图片-Service

	@Autowired
	private ApartmentFacilityService apartmentFacilityService;//配套-Service

	@Autowired
	private ApartmentLabelService apartmentLabelService;//标签-Service

	@Autowired
	private ApartmentFeeValueService apartmentFeeValueService;//杂费-Service

	@Autowired
	private ApartmentInfoMapper apartmentInfoMapper;//公寓-Mapper

	@Autowired
	private GraphInfoMapper graphInfoMapper;//图片-Mapper

	@Autowired
	private LabelInfoMapper labelInfoMapper;//标签-Mapper

	@Autowired
	private FacilityInfoMapper facilityInfoMapper;//配套-Mapper

	@Autowired
	private FeeValueMapper feeValueMapper;//杂费-Mapper

	@Autowired
	private RoomInfoMapper roomInfoMapper;//房间-Mapper

	//保存或更新公寓信息
	@Override
	public void saveOrUpdateApart(ApartmentSubmitVo apartmentSubmitVo) {
		Boolean isUpdate = apartmentSubmitVo.getId() != null;
		super.saveOrUpdate(apartmentSubmitVo);//填入公寓的基本信息

		//是修改直接全部删除，再添加
		if (isUpdate) {//修改
			//1.删除图片列表
			LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<GraphInfo>()
					.eq(GraphInfo::getItemType, ItemType.APARTMENT)//公寓
					.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());//id
			graphInfoService.remove(graphInfoLambdaQueryWrapper);
			//2.删除配套列表
			LambdaQueryWrapper<ApartmentFacility> apartmentFacilityLambdaQueryWrapper = new LambdaQueryWrapper<ApartmentFacility>()
					.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
			apartmentFacilityService.remove(apartmentFacilityLambdaQueryWrapper);

			//3.删除标签列表
			LambdaQueryWrapper<ApartmentLabel> apartmentLabelLambdaQueryWrapper = new LambdaQueryWrapper<ApartmentLabel>()
					.eq(ApartmentLabel::getApartmentId, apartmentSubmitVo.getId());
			apartmentLabelService.remove(apartmentLabelLambdaQueryWrapper);
			//4.删除杂费列表
			LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueLambdaQueryWrapper = new LambdaQueryWrapper<ApartmentFeeValue>()
					.eq(ApartmentFeeValue::getApartmentId, apartmentSubmitVo.getId());
			apartmentFeeValueService.remove(apartmentFeeValueLambdaQueryWrapper);
		}

		//1.插入图片列表
		List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
		//非空判断
		if (!CollectionUtils.isEmpty(graphVoList)) {
			List<GraphInfo> graphInfoList = new ArrayList<>();
			for (GraphVo graphVo : graphVoList) {
				//补全信息
				GraphInfo graphInfo = new GraphInfo();
				graphInfo.setItemType(ItemType.APARTMENT);
				graphInfo.setItemId(apartmentSubmitVo.getId());
				graphInfo.setName(graphVo.getName());
				graphInfo.setUrl(graphVo.getUrl());

				//添加到集合中
				graphInfoList.add(graphInfo);
			}
			graphInfoService.saveBatch(graphInfoList);
		}
		//2.插入配套列表
		List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
		if (!CollectionUtils.isEmpty(facilityInfoIds)) {
			List<ApartmentFacility> apartmentFacilityList = new ArrayList<>();
			for (Long facilityInfoId : facilityInfoIds) {
				ApartmentFacility apartmentFacility = new ApartmentFacility();
				apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
				apartmentFacility.setFacilityId(facilityInfoId);
				apartmentFacilityList.add(apartmentFacility);
			}
			apartmentFacilityService.saveBatch(apartmentFacilityList);
		}
		//3.插入标签列表
		List<Long> labelIds = apartmentSubmitVo.getLabelIds();
		if (!CollectionUtils.isEmpty(labelIds)) {
			List<ApartmentLabel> apartmentLabelList = new ArrayList<>();
			for (Long labelId : labelIds) {
				ApartmentLabel apartmentLabel = new ApartmentLabel();
				apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
				apartmentLabel.setLabelId(labelId);
				apartmentLabelList.add(apartmentLabel);
			}
			apartmentLabelService.saveBatch(apartmentLabelList);
		}
		//4.插入杂费列表
		List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
		if (!CollectionUtils.isEmpty(feeValueIds)) {
			List<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
			for (Long feeValueId : feeValueIds) {
				ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
				apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
				apartmentFeeValue.setFeeValueId(feeValueId);
				apartmentFeeValueList.add(apartmentFeeValue);
			}
			apartmentFeeValueService.saveBatch(apartmentFeeValueList);
		}

	}

	//根据条件分页查询公寓列表
	@Override
	public IPage<ApartmentItemVo> pageApartmentQueryVo(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {

		return apartmentInfoMapper.pageApartmentQueryVo(page, queryVo);
	}

	//根据ID获取公寓详细信息
	@Override
	public ApartmentDetailVo getDApartmentDetailVoById(Long id) {
		//1.查询ApartmentInfo
		ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
		if (apartmentInfo == null) {//非空判断
			return null;
		}

		//2.查询GraphInfo
		List<GraphVo> graphVoList = graphInfoMapper.getGraphInfoByItemTypeAndId(ItemType.APARTMENT, id);
		//3.查询LabelInfo
		List<LabelInfo> labelInfoList = labelInfoMapper.getLabelInfoListById(id);
		//4.查询FacilityInfo
		List<FacilityInfo> facilityInfoList = facilityInfoMapper.getFacilityInfoListById(id);
		//5.查询FeeValue
		List<FeeValueVo> feeValueVoList = feeValueMapper.getfeeValueVoListById(id);

		//6.封装返回ApartmentDetailVo
		ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
		BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
		apartmentDetailVo.setGraphVoList(graphVoList);
		apartmentDetailVo.setLabelInfoList(labelInfoList);
		apartmentDetailVo.setFacilityInfoList(facilityInfoList);
		apartmentDetailVo.setFeeValueVoList(feeValueVoList);
		return apartmentDetailVo;
	}

	//根据id删除公寓信息
	@Override
	@Transactional//事件管理
	public void removeApartmentInfoById(Long id) {
		//删除公寓，先看该公寓下面是否有房间
		LambdaQueryWrapper<RoomInfo> roomInfoLambdaQueryWrapper = new LambdaQueryWrapper<RoomInfo>()
				.eq(RoomInfo::getApartmentId, id);
		if (roomInfoMapper.selectCount(roomInfoLambdaQueryWrapper) > 0) {
			//公寓下有房间
			throw new LeaseException(ResultCodeEnum.DELETE_ERROR);//异常处理
		}
		//没有房间,删除公寓
		super.removeById(id);//直接调用父类的方法

		//删除公寓下面的配套资源
		//1.删除图片列表
		LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<GraphInfo>()
				.eq(GraphInfo::getItemType, ItemType.APARTMENT)//公寓
				.eq(GraphInfo::getItemId, id);
		graphInfoService.remove(graphInfoLambdaQueryWrapper);
		//2.删除配套列表
		LambdaQueryWrapper<ApartmentFacility> apartmentFacilityLambdaQueryWrapper = new LambdaQueryWrapper<ApartmentFacility>()
				.eq(ApartmentFacility::getApartmentId, id);
		apartmentFacilityService.remove(apartmentFacilityLambdaQueryWrapper);

		//3.删除标签列表
		LambdaQueryWrapper<ApartmentLabel> apartmentLabelLambdaQueryWrapper = new LambdaQueryWrapper<ApartmentLabel>()
				.eq(ApartmentLabel::getApartmentId, id);
		apartmentLabelService.remove(apartmentLabelLambdaQueryWrapper);
		//4.删除杂费列表
		LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueLambdaQueryWrapper = new LambdaQueryWrapper<ApartmentFeeValue>()
				.eq(ApartmentFeeValue::getApartmentId, id);
		apartmentFeeValueService.remove(apartmentFeeValueLambdaQueryWrapper);

	}
}




