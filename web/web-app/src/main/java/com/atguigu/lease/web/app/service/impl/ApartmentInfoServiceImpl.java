package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
		implements ApartmentInfoService {


	@Autowired
	private ApartmentInfoMapper apartmentInfoMapper;//公寓

	@Autowired
	private LabelInfoMapper labelInfoMapper;//标签信息

	@Autowired
	private GraphInfoMapper graphInfoMapper;//图片信息

	@Autowired
	private RoomInfoMapper roomInfoMapper;//房间

	@Autowired
	private FacilityInfoMapper facilityInfoMapper;//配套

	@Override
	@Transactional
	public ApartmentItemVo getApartmentItemVoByroomId(Long apartmentId) {
		ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(apartmentId);//公寓基本信息

		//标签信息列表
		List<LabelInfo> labelInfoList = labelInfoMapper.getLabelInfoByApartmentId(apartmentId);

		//图片信息列表
		List<GraphVo> graphVoList = graphInfoMapper.getgraphVoList(ItemType.APARTMENT, apartmentId);

		//最低租金
		BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(apartmentId);

		//封装返回结果
		ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
		BeanUtils.copyProperties(apartmentInfo, apartmentItemVo);
		apartmentItemVo.setLabelInfoList(labelInfoList);
		apartmentItemVo.setGraphVoList(graphVoList);
		apartmentItemVo.setMinRent(minRent);
		return apartmentItemVo;
	}

	//根据id获取公寓信息
	@Override
	public ApartmentDetailVo getDetailById(Long id) {
		ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
		if (apartmentInfo == null) {
			return null;
		}
		//图片信息列表
		List<GraphVo> graphVoList = graphInfoMapper.getgraphVoList(ItemType.APARTMENT, id);

		//标签信息列表
		List<LabelInfo> labelInfoList = labelInfoMapper.getLabelInfoByApartmentId(id);

		//配套列表
		List<FacilityInfo> facilityInfoList = facilityInfoMapper.getFacilityInfoListByApartmentId(id);

		//租金最小值
		BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);

		//封装返回结果
		ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
		BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
		apartmentDetailVo.setGraphVoList(graphVoList);
		apartmentDetailVo.setLabelInfoList(labelInfoList);
		apartmentDetailVo.setFacilityInfoList(facilityInfoList);
		apartmentDetailVo.setMinRent(minRent);
		return apartmentDetailVo;
	}
}




