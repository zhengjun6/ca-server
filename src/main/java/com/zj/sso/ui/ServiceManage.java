package com.zj.sso.ui;

import java.io.IOException;
import java.net.URL;

import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceManage {
	  @Autowired
	  @Qualifier("servicesManager")
	  private ServicesManager servicesManager;
	  
	  /**
		 * 加入新的的cas客户端，只要是域名和端口号，不需要前缀，如http://或者https://，这边增加了单点退出功能，cas退出默认使用隐式退出
		 * protocol 代表的是协议，只能是http或者是https的协议
		 */
		@RequestMapping(value = "/addClient/{protocol}/{serviceId}/{id}",method = RequestMethod.GET) 
		  public String addClient(@PathVariable("serviceId") String serviceId,@PathVariable("protocol") String protocol
				  ,@PathVariable("id") int id) throws IOException {
			  String url=protocol+"://"+serviceId;
			  RegisteredService svc = servicesManager.findServiceBy(url);
			  if(svc!=null){
				  return "0";//0代表着已存在这个服务，服务是通过正则去匹配的，所以这边建议使用ip或者域名+端口号
			  }
			  String a="^"+url+".*";//匹配以这个url开始的url,这个视具体场景
			  RegexRegisteredService service=new RegexRegisteredService();
			  ReturnAllAttributeReleasePolicy re=new ReturnAllAttributeReleasePolicy();
			  service.setServiceId(a);
			  service.setId(id);
			  service.setAttributeReleasePolicy(re);
			  //将name统一设置为servicesId
			  service.setName(serviceId);
			  service.setLogoutUrl(new URL(url));//这个是为了单点登出而作用的
			  servicesManager.save(service,true);
			  servicesManager.load();
			  return "1";//添加服务成功
		}
		//删除服务
		@RequestMapping(value = "/delete/{serviceId}",method = RequestMethod.GET) 
		public String delOauthService(@PathVariable("serviceId") String serviceId) {
			String res="";
		    RegisteredService svc = servicesManager.findServiceBy(serviceId);
		    if(svc!=null){
		    	try {
		    		 servicesManager.delete(svc);
		    	    } catch (Exception e) {
		    	    	//因为我们这边没有审计的功能，所以即使删除成功也会抛异常，直接捕获
		    	      if (null == servicesManager.findServiceBy(serviceId)) {
		    	        res = "success";
		    	        servicesManager.load();
		    	      } else {
		    	        res = "failed";
		    	      }
		    }
		}
		    return res;
	}
}