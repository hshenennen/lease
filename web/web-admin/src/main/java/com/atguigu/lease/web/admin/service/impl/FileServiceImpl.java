package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
	@Autowired
	private MinioProperties properties;// MinIO配置属性

	@Autowired
	private MinioClient client;// MinIO客户端实例

	//上传文件
	@Override
	public String upload(MultipartFile file) {
		try {
			// 检查并创建Bucket
			boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());// // 创建Bucket
			if (!bucketExists) {
				client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
				client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(properties.getBucketName()).config(createBucketPolicyConfig(properties.getBucketName())).build());
			}

			// 生成唯一文件名 -- 路径结构：日期/UUID-原始文件名
			String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
			//文件上传
			client.putObject(PutObjectArgs.builder().
					bucket(properties.getBucketName()).//bucket：存储桶名称
					object(filename).//object：对象（文件）路径
					stream(file.getInputStream(), file.getSize(), -1).//stream：文件输入流、大小、分片大小（-1表示不分片）
					contentType(file.getContentType()).build());//contentType：保留原始文件类型

			return String.join("/", properties.getEndpoint(), properties.getBucketName(), filename);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//Bucket策略
	private String createBucketPolicyConfig(String bucketName) {
		//允许所有人读取：Principal: "*" 表示所有用户
		//只读权限：只允许GetObject操作，不能上传或删除
		//资源范围：仅限该Bucket下的所有对象
		return """
				{
				  "Statement" : [ {
				    "Action" : "s3:GetObject",
				    "Effect" : "Allow",
				    "Principal" : "*",
				    "Resource" : "arn:aws:s3:::%s/*"
				  } ],
				  "Version" : "2012-10-17"
				}
				""".formatted(bucketName);
	}
}

