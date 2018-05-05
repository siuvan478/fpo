package com.fpo.controller;

import com.fpo.base.ResultData;
import com.fpo.model.AttachmentParam;
import com.fpo.service.AttachmentService;
import com.fpo.utils.LoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 保存附件
     *
     * @param attachmentParam
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/attachment/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Long> save(@RequestBody AttachmentParam attachmentParam)
            throws Exception {
        attachmentParam.setUserId(LoginUtil.getUserId());
        return new ResultData<>(this.attachmentService.save(attachmentParam));
    }

    /**
     * 删除附件
     *
     * @param attachmentParam
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/attachment/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Void> delete(@RequestBody AttachmentParam attachmentParam)
            throws Exception {
        attachmentService.delete(attachmentParam);
        return new ResultData<>();
    }

}
