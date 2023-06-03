package com.it.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.it.dao.*;
import com.it.entity.*;
import com.it.util.FileUploadUtil;
import com.it.util.FtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


/**
 * 上传图片公共接口等工具类
 * @author Administrator
 *
 */
@Controller  
public class UtilController  extends BaseController {
	//数据库切换
	//DataSourceContextHolder.setCustomerType("dataSourceThree");
	//切换后关闭
	//DataSourceContextHolder.clearCustomerType();
    @Resource
    JobDAO jobDAO;
    @Resource
    MemberDAO memberDAO;
    @Resource
    AreaDAO areaDAO;
    @Resource
    CategoryDAO categoryDAO;
    @Resource
    ApplyDAO applyDAO;
	//上传图片
	@ResponseBody
	@RequestMapping("admin/uploadImg")
	public Map uploadImg(MultipartFile file,HttpServletRequest request) {
		String prefix="";
        String dateStr="";
        //保存上传
        OutputStream out = null;
        InputStream fileInput=null;
        try{
            if(file!=null){
                String originalName = file.getOriginalFilename();
                prefix=originalName.substring(originalName.lastIndexOf(".")+1);
                Date date = new Date();
                String uuid = UUID.randomUUID()+"";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateStr = simpleDateFormat.format(date);
                //String filepath = "D:\\ebooksysimages\\" + dateStr+"\\"+uuid+"." + prefix;
                String filepath = request.getRealPath("/upload/")+"/"+uuid+"." + prefix;
//                System.out.println("filepath==="+filepath);

                File files=new File(filepath);
                //打印查看上传路径
//                System.out.println(filepath);
                if(!files.getParentFile().exists()){
                    files.getParentFile().mkdirs();
                }
                file.transferTo(files);
                Map<String,Object> map2=new HashMap<>();
                Map<String,Object> map=new HashMap<>();
                map.put("code",0);
                map.put("msg","");
                map.put("data",map2);
                //map2.put("src","/images/"+ dateStr+"/"+uuid+"." + prefix);
                map2.put("src",uuid+"." + prefix);
                return map;
            }

        }catch (Exception e){
        }finally{
            try {
                if(out!=null){
                    out.close();
                }
                if(fileInput!=null){
                    fileInput.close();
                }
            } catch (IOException e) {
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("code",1);
        map.put("msg","");
        return map;
	}


	//热门岗位
    @ResponseBody
    @RequestMapping("hotJob")
    public HashMap<String,Object> hotJob(@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum,@RequestParam(defaultValue = "1",value = "pageSize") Integer pageSize,HttpServletRequest request){
        HashMap<String,Object> res = new HashMap<String,Object>();
        HashMap map = new HashMap();
        List<Apply> joblist = applyDAO.selectHot(map);
        for(Apply apply:joblist){
            Job job = jobDAO.findById(apply.getJobid());
            Area area = areaDAO.findById(job.getAreaid());
            job.setArea(area);
            Category category = categoryDAO.findById(job.getCategoryid());
            job.setCategory(category);
            Member company = memberDAO.findById(job.getCompanyid());
            job.setCompany(company);
            apply.setJob(job);
        }
        res.put("list", joblist);
        return res;
    }

}
