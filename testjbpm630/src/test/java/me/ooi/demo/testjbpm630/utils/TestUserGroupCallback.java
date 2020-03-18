package me.ooi.demo.testjbpm630.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.task.UserGroupCallback;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Scope(value=BeanDefinition.SCOPE_SINGLETON)
@Component
public class TestUserGroupCallback implements UserGroupCallback {
	
	//测试数据
	public static Map<String, List<String>> userGroups = new LinkedHashMap<String, List<String>>() ; 
	static {
		userGroups.put("u1", Collections.singletonList("g1")) ; 
		userGroups.put("u2", Collections.singletonList("g2")) ; 
		userGroups.put("u3", Collections.singletonList("g3")) ; 
		userGroups.put("u4", Collections.singletonList("g4")) ; 
		userGroups.put("u5", Collections.singletonList("g5")) ;
	}

	@Override  
    public boolean existsUser(String userId) {  
        return true;  
    }

	@Override  
    public boolean existsGroup(String groupId) {  
        return true;  
    }

	@Override  
    public List<String> getGroupsForUser(String userId, List<String> groupIds,  
            List<String> allExistingGroupIds) {  
        return userGroups.get(userId) ; 
		/*
		if (groupIds != null) {  
  
            List<String> retList = new ArrayList<String>(groupIds);  
  
            // merge all groups  
  
            if (allExistingGroupIds != null) {  
  
                for (String grp : allExistingGroupIds) {  
  
                    if (!retList.contains(grp)) {  
  
                        retList.add(grp);  
  
                    }  
  
                }  
  
            }  
  
            return retList;  
  
        } else {  
        	
        	if( userId.equals("u5") ){
        		return Collections.singletonList("g2") ; 
        	}
        	
            //  
            // return empty list by default  
            // please note: there are different return value for different  
            // version of jPBM  
            // List<String> retList = new ArrayList<String>();  
  
            // retList.add("user");  
            // return retList;  
            // return new ArrayList<String>(); //for jBPM5.3.0.Final  
            return null; // for jBPM5.4.0.CR1  
  
        }  
        */
    }  
} 