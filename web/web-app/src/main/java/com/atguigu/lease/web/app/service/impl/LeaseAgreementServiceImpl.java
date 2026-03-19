package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
		implements LeaseAgreementService {

	@Autowired
	private LeaseAgreementMapper leaseAgreementMapper;//租约信息

	@Autowired
	private ApartmentInfoMapper apartmentInfoMapper;//公寓

	@Autowired
	private GraphInfoMapper graphInfoMapper;//图片

	@Autowired
	private RoomInfoMapper roomInfoMapper;//房间

	@Autowired
	private PaymentTypeMapper paymentTypeMapper;//支付方式

	@Autowired
	private LeaseTermMapper leaseTermMapper;//租期

	//获取个人租约基本信息列表
	@Override
	public List<AgreementItemVo> getAgreementItemVoListByPhone(String phone) {
		return leaseAgreementMapper.getAgreementItemVoListByPhone(phone);
	}

	//根据id获取租约详细信息
	@Override
	public AgreementDetailVo getAgreementDetailVoById(Long id) {
		//租约信息
		LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);
		//非空判断
		if (leaseAgreement == null) {
			return null;
		}
		//公寓
		ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId());
		//公寓名称
		String apartmentName = apartmentInfo.getName();
		//公寓图片列表
		List<GraphVo> apartmentGraphVoList = graphInfoMapper.getgraphVoList(ItemType.APARTMENT, apartmentInfo.getId());
		//房间号
		RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());
		String roomNumber = roomInfo.getRoomNumber();
		//房间图片列表
		List<GraphVo> roomGraphVoList = graphInfoMapper.getgraphVoList(ItemType.ROOM, roomInfo.getId());
		//支付方式
		PaymentType paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());
		String paymentTypeName = paymentType.getName();
		//租期月数
		LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());
		Integer leaseTermMonthCount = leaseTerm.getMonthCount();
		//租期单位
		String leaseTermUnit = leaseTerm.getUnit();

		//封装返回结果
		AgreementDetailVo agreementDetailVo = new AgreementDetailVo();
		BeanUtils.copyProperties(leaseAgreement, agreementDetailVo);
		agreementDetailVo.setApartmentName(apartmentName);
		agreementDetailVo.setApartmentGraphVoList(apartmentGraphVoList);
		agreementDetailVo.setRoomNumber(roomNumber);
		agreementDetailVo.setRoomGraphVoList(roomGraphVoList);
		agreementDetailVo.setPaymentTypeName(paymentTypeName);
		agreementDetailVo.setLeaseTermMonthCount(leaseTermMonthCount);
		agreementDetailVo.setLeaseTermUnit(leaseTermUnit);


		return agreementDetailVo;
	}
}




