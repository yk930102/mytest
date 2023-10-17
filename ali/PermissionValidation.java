package com.zt.security.controller;

import com.zt.anno.Permission;
import com.zt.assistant.helper.CommonHelper;
import com.zt.assistant.helper.Success;
import com.zt.util.sessionFactory.BaseSessionFactory;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@Order(1)
public class PermissionValidation {
    @Autowired
    private BaseSessionFactory sessionFactory;

    @Pointcut("@annotation(com.zt.anno.Permission)")
    public void permissionPointCut() {}

	@Around("permissionPointCut()")
	public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("before - 权限检查 === 执行方法:" + joinPoint.getSignature().getName());

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletResponse response = servletRequestAttributes.getResponse();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        response.setContentType("text/json;charset=utf-8");
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Permission permission = method.getAnnotation(Permission.class);
		String permissionId = permission.value();
		String orgKey = permission.orgKey();

        if(validate(request,permissionId,orgKey)) {
            Object proceed = joinPoint.proceed();
            return proceed;
        }

        Success success = new Success();
        success.setSuccess(true);
        success.setRemark("没有权限");
        success.setMsg("N");
        if(method.getReturnType() == void.class){
            CommonHelper.responseToFront(response,success);
            return true;
        }
        return success;

	}
	private boolean validate( HttpServletRequest request,String permissionId,String orgKey) throws IOException {
        List permissionList = (List)request.getSession().getAttribute("permission");// 权限列表
        boolean hasPermission = true;
        if(StringUtils.isNotBlank(permissionId)){//需要权限
            if(StringUtils.isNotBlank(orgKey)){//需要相关组织权限
                if(StringUtils.isBlank(request.getParameter(orgKey))){
                    return false;
                }
                Session sessionSlave1 = sessionFactory.getCurrentSessionSlave1();
                hasPermission = CommonHelper.hasOrgPermissionByPermissionlist(sessionSlave1,permissionList, permissionId,request.getParameter(orgKey));
            }else {
                hasPermission = CommonHelper.hasPermissionByPermissionList(permissionList, permissionId);
            }
        }
        return hasPermission;
    }
}
