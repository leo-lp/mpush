package com.shinemo.mpush.tools.redis;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

import com.google.common.collect.Lists;

import redis.clients.jedis.Jedis;

public class RedisUtilTest {

	RedisNode node = new RedisNode("127.0.0.1", 6379, "ShineMoIpo");
	RedisNode node2 = new RedisNode("127.0.0.1", 6380, "ShineMoIpo");
	
	
	List<RedisNode> nodeList = Lists.newArrayList(node,node2);
	
	@Test
	public void testAddAndGetAndDelete(){
		Jedis jedis = RedisUtil.getClient(node2);
		jedis.set("hi", "huang");
		
		String ret = jedis.get("hi");
		System.out.println(ret);
		
		jedis.del("hi");
		ret = jedis.get("hi");
		if(ret==null){
			System.out.println("ret is null");
		}else{
			System.out.println("ret is not null:"+ret);
		}
		
	}
	
	@Test
	public void testJedisPool(){
		//最大连接数是8，因此，获取10个链接会抛错误
		List<Jedis> jedisList = Lists.newArrayList();
		for(int i = 0;i<10;i++){
			Jedis jedis = RedisUtil.getClient(node);
			jedisList.add(jedis);
		}
	}
	
	@Test
	public void testJedisPool2(){
		//最大连接数是8，因此，获取10个链接会抛错误
		List<Jedis> jedisList = Lists.newArrayList();
		for(int i = 1;i<=8;i++){
			Jedis jedis = RedisUtil.getClient(node);
			jedisList.add(jedis);
		}
		
		System.out.println(jedisList.size());
		
		try{
			Jedis jedis = RedisUtil.getClient(node);
			jedisList.add(jedis);
			System.out.println("first get jedis success");
		}catch(Exception e){
			System.out.println(e);
		}
		
		//关闭一个链接
		RedisUtil.close(jedisList.get(0));
		
		try{
			Jedis jedis = RedisUtil.getClient(node);
			jedisList.add(jedis);
			System.out.println("second get jedis success");
		}catch(Exception e){
			System.out.println(e);
		}
		
		System.out.println(jedisList.size());
	}
	
	
	@Test
	public void testKV(){
		User user = new User("huang", 18, new Date());
		RedisUtil.set(nodeList, "test", user);
		
		User nowUser = RedisUtil.get(node, "test", User.class);
		System.out.println("node1:"+ToStringBuilder.reflectionToString(nowUser));
		
		nowUser = RedisUtil.get(node2, "test", User.class);
		System.out.println("node2:"+ToStringBuilder.reflectionToString(nowUser));
		
		RedisUtil.del(nodeList, "test");
		
		nowUser = RedisUtil.get(node2, "test", User.class);
		if(nowUser==null){
			System.out.println("node2 nowUser is null");
		}else{
			System.out.println("node2:"+ToStringBuilder.reflectionToString(nowUser));
		}
		
		
		nowUser = RedisUtil.get(node, "test", User.class);
		if(nowUser==null){
			System.out.println("node nowUser is null");
		}else{
			System.out.println("node:"+ToStringBuilder.reflectionToString(nowUser));
		}
		
		RedisUtil.set(nodeList, "test", user,10);
		
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		nowUser = RedisUtil.get(node2, "test", User.class);
		if(nowUser==null){
			System.out.println("node2 nowUser is null");
		}else{
			System.out.println("node2:"+ToStringBuilder.reflectionToString(nowUser));
		}
		
		
		nowUser = RedisUtil.get(node, "test", User.class);
		if(nowUser==null){
			System.out.println("node nowUser is null");
		}else{
			System.out.println("node:"+ToStringBuilder.reflectionToString(nowUser));
		}
		
	}
	
	
}
