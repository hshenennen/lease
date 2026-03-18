package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.app.mapper.GraphInfoMapper;
import com.atguigu.lease.web.app.mapper.LabelInfoMapper;
import com.atguigu.lease.web.app.mapper.RoomInfoMapper;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
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
}




