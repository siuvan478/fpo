package com.fpo.utils;

import com.fpo.annotation.Constant;
import com.fpo.annotation.DictGroup;
import com.fpo.vo.DictVO;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/4/21 0021.
 */
@Component
public class DicUtil {

    private static final Map<String, List<DictVO>> dictMap = Maps.newHashMapWithExpectedSize(10);

    private static final String BASE_PACKAGE = "com.fpo.constant";

    private static ClassPathScanningCandidateComponentProvider provider = null;

    public static List<DictVO> getDictVOs(String dictKey) {
        List<DictVO> dictVOs = dictMap.get(dictKey);
        if (CollectionUtils.isNotEmpty(dictVOs)) {
            return BeanMapper.mapList(dictVOs, DictVO.class);
        }
        return null;
    }

    public static String getDictValue(String dictKey, Integer key) {
        if (key == null || StringUtils.isBlank(dictKey)) return StringUtils.EMPTY;
        return getDictValue(dictKey, key.toString());
    }

    public static String getDictValue(String dictKey, String key) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(dictKey)) return StringUtils.EMPTY;
        List<DictVO> dictVOs = getDictVOs(dictKey);
        if (CollectionUtils.isEmpty(dictVOs)) return StringUtils.EMPTY;
        StringBuilder sb = new StringBuilder();
        String[] keyArray = key.split(",");
        for (String k : keyArray) {
            for (DictVO dictVO : dictVOs) {
                if (dictVO.getKey().equals(k)) {
                    if (StringUtils.isBlank(sb.toString())) {
                        sb.append(dictVO.getValue());
                    } else {
                        sb.append(",").append(dictVO.getValue());
                    }
                    break;
                }
            }
        }

        return sb.toString();
    }

    public static boolean validate(String dictKey, Integer key) {
        if (key == null || StringUtils.isBlank(dictKey)) return false;
        return validate(dictKey, key.toString());
    }

    public static boolean validate(String dictKey, String key) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(dictKey)) return false;
        List<DictVO> dictVOs = getDictVOs(dictKey);
        if (CollectionUtils.isEmpty(dictVOs)) return false;

        int equalsSize = 0;
        String[] keyArray = key.split(",");
        for (String k : keyArray) {
            for (DictVO dictVO : dictVOs) {
                if (dictVO.getKey().equals(k)) {
                    equalsSize++;
                    break;
                }
            }
        }

        return equalsSize == keyArray.length;
    }

    public DicUtil() {
        init();
    }

    private void init() {
        try {
            provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AnnotationTypeFilter(Constant.class));
            Set<BeanDefinition> candidateComponents = provider.findCandidateComponents(BASE_PACKAGE);
            for (BeanDefinition b : candidateComponents) {
                Class<?> clazz = Class.forName(b.getBeanClassName());
                for (Field field : clazz.getFields()) {
                    if (field.isAnnotationPresent(DictGroup.class)) {
                        DictGroup dictGroup = field.getAnnotation(DictGroup.class);
                        if (dictMap.get(dictGroup.groupName()) == null) {
                            dictMap.put(dictGroup.groupName(), new ArrayList<>());
                        }
                        dictMap.get(dictGroup.groupName()).add(new DictVO(dictGroup.key(), dictGroup.value()));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
