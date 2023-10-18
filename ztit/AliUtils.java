package com.zt.alismsv2;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.Common;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class AliUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String sendSms(String phoneNumbers,String content) throws JsonProcessingException {
        return sendSms(phoneNumbers,content,AliClient.templateCode,AliClient.templateParam);
    }

    public static String sendSms(String phoneNumbers,String content,String templateCode,String templateParam) throws JsonProcessingException {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put(templateParam, content);
        String jsonStr = mapper.writeValueAsString(contentMap);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumbers)
                .setSignName(AliClient.signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(jsonStr);
        try {
            SendSmsResponse sendSmsResponse  = AliClient.getInstance().sendSms(sendSmsRequest);
            return sendSmsResponse.getBody().getMessage();
        } catch (TeaException error) {
            return Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            return Common.assertAsString(error.message);
        }
    }
}
