package com.atguigu.lease.web.admin.schedule;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTasks {

//	@Scheduled(cron = "* * * * * *")
//	public void test(){
//		System.out.println(new Date());
//	}

	@Autowired
	private LeaseAgreementService leaseAgreementService;

	@Scheduled(cron = "0 0 0 * * *")//每天的0时0分0秒
	public void checkLeaseStatus() {
		LambdaUpdateWrapper<LeaseAgreement> leaseAgreementLambdaUpdateWrapper = new LambdaUpdateWrapper<LeaseAgreement>()
				.le(LeaseAgreement::getLeaseEndDate, new Date())//le是小于等于
				.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING)//in和数据库一样，in(1,2,3) 多个参数
				.set(LeaseAgreement::getStatus, LeaseStatus.EXPIRED);
		leaseAgreementService.update(leaseAgreementLambdaUpdateWrapper);
	}
}
