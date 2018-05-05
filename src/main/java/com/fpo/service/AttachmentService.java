package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.constant.DictConstants;
import com.fpo.constant.GlobalConstants;
import com.fpo.mapper.AttachmentMapper;
import com.fpo.model.Attachment;
import com.fpo.model.AttachmentParam;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.LoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    public Long save(AttachmentParam attachmentParam) {
        Attachment attachment = BeanMapper.map(attachmentParam, Attachment.class);
        attachment.setUserId(LoginUtil.getUserId());
        attachment.setUserId(6L);
        attachmentMapper.insert(attachment);
        return attachment.getId();
    }

    public void delete(AttachmentParam attachmentParam) throws Exception {
        if (attachmentParam.getId() == null) throw new BaseException("附件ID不能为空");
        Attachment attachment = attachmentMapper.selectByPrimaryKey(attachmentParam.getId());
        if (attachment != null && attachment.getUserId().equals(LoginUtil.getUserId())) {
            attachment.setStatus(GlobalConstants.State.DELETED);
            attachmentMapper.updateByPrimaryKey(attachment);
        }
    }
}
