package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.constant.DictConstants;
import com.fpo.constant.GlobalConstants;
import com.fpo.mapper.AttachmentMapper;
import com.fpo.model.Attachment;
import com.fpo.model.AttachmentParam;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.DicUtil;
import com.fpo.utils.LoginUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    /**
     * 保存附件信息
     *
     * @param attachmentParam
     * @return
     */
    public Long save(AttachmentParam attachmentParam) throws Exception {
        if (attachmentParam.getBizId() == null) throw new BaseException("bizId参数不能空");
        if (attachmentParam.getBizType() == null) throw new BaseException("附件类型不能空");
        if (!DicUtil.validate(DictConstants.BIZ_TYPE_DICT_KEY, attachmentParam.getBizType())) {
            throw new BaseException("不支持的附件类型");
        }
        if (StringUtils.isBlank(attachmentParam.getName())) {
            throw new BaseException("附件名称不能空");
        }
        if (StringUtils.isBlank(attachmentParam.getSuffix())) {
            throw new BaseException("附件后缀名不能空");
        }
        if (StringUtils.isBlank(attachmentParam.getPath())) {
            throw new BaseException("oosKey不能空");
        }
        Attachment attachment = BeanMapper.map(attachmentParam, Attachment.class);
        this.attachmentMapper.insert(attachment);
        return attachment.getId();
    }

    /**
     * 删除附件
     *
     * @param attachmentParam
     * @throws Exception
     */
    public void delete(AttachmentParam attachmentParam) throws Exception {
        if (attachmentParam.getId() == null) throw new BaseException("附件ID不能为空");
        Attachment attachment = attachmentMapper.selectByPrimaryKey(attachmentParam.getId());
        if (attachment != null && attachment.getUserId().equals(LoginUtil.getUserId())) {
            attachment.setStatus(GlobalConstants.State.DELETED);
            attachmentMapper.updateByPrimaryKey(attachment);
        }
    }

    /**
     * 更新附件业务ID
     *
     * @param bizId
     * @param attIdList
     */
    public void updateBizIdByCondition(Long bizId, Integer bizType, List<Long> attIdList) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("bizId", bizId);
        map.put("bizType", bizType);
        map.put("attIdList", attIdList);
        this.attachmentMapper.updateBizIdByCondition(map);
    }

    /**
     * 删除附件
     *
     * @param bizId
     * @param bizType
     */
    public void deleteByBizIdAndType(Long bizId, Integer bizType) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("bizId", bizId);
        map.put("bizType", bizType);
        this.attachmentMapper.deleteByBizIdAndType(map);
    }
}
