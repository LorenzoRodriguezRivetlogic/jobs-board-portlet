package com.rivetlogic.jobsboard.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

public class FiltersUtil {
    
    public static final String ROLE = "Jobs Manager";
    public static final String ALL = "all";

    public static String[] processKeywords(String keywords) {
        String[] list = keywords.split(" ");
        for(int i = 0; i < list.length; i++) {
            list[i] = StringPool.PERCENT + list[i] + StringPool.PERCENT;
        }
        return list;
    }
    
    // TODO: Always return only active if the user is not logged/admin
    public static boolean[] getStatus(RenderRequest req, ThemeDisplay themeDisplay) {
        String value = ParamUtil.getString(req, "status", ALL);
        if(!themeDisplay.isSignedIn()) { // || doesn't have role....
            return new boolean[] { true };
        }else if(Validator.equals(value, ALL)) {
            return new boolean[]{ true, false };
        } else {
            return new boolean[]{ Boolean.parseBoolean(value) };
        }
    }
    
    public static long[] getFilter(RenderRequest req, String name, List<AssetCategory> categories) {
       if(Validator.isNull(categories)) {
           return new long[] {};
       }
       String value = ParamUtil.getString(req, name);
       if(Validator.isNull(value) || Validator.equals(value, StringPool.BLANK) || Validator.equals(value, ALL)) {
           List<Long> ids = new ArrayList<Long>();
           for(AssetCategory cat : categories) {
               ids.add(cat.getCategoryId());
           }
           return ArrayUtil.toArray(ids.toArray(new Long[]{}));
       } else {
           return new long[] { Long.parseLong(value)};
       }
    }
    
    public static List<AssetCategory> getCategories(PortletRequest req, String name) throws PortalException, SystemException {
        long vocabularyId = getCategoryId(req, name);
        AssetVocabulary vocabulary = AssetVocabularyLocalServiceUtil
                .fetchAssetVocabulary(vocabularyId);
        return vocabulary.getCategories();
    }
    
    public static long getCategoryId(PortletRequest req, String name) {
        PortletPreferences prefs = req.getPreferences();
        return GetterUtil.getLong(prefs.getValue(name, "-1L"));
    }
    
    public static boolean viewAdmin(ThemeDisplay themeDisplay) throws SystemException {
        if(themeDisplay.isSignedIn()) {
            User user = themeDisplay.getUser();
            for(Role role : user.getRoles()) {
                if(Validator.equals(role.getName(), ROLE)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
    
}
