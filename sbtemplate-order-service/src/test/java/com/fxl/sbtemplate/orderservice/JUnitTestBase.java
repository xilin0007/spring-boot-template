package com.fxl.sbtemplate.orderservice;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description JUnitTestBase
 * @author fangxilin
 * @date 2020-08-18
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes={OrderServiceApplication.class})// 指定启动类
@EnableAutoConfiguration
public class JUnitTestBase extends TestCase {

}
