package org.eredlab.g4.eastelsoft.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;
import org.eredlab.g4.ccl.datastructure.Dto;
import org.eredlab.g4.ccl.datastructure.impl.BaseDto;
import org.eredlab.g4.ccl.id.UUIDGenerator;
import org.eredlab.g4.ccl.json.JsonHelper;
import org.eredlab.g4.ccl.util.G4Utils;
import org.eredlab.g4.eastelsoft.service.VisitService;
import org.eredlab.g4.eastelsoft.util.WzlConstants;
import org.eredlab.g4.eastelsoft.vo.UploadImgBean;
import org.eredlab.g4.eastelsoft.vo.VisitBean;
import org.eredlab.g4.eastelsoft.vo.VisitEvaluateBean;
import org.eredlab.g4.eastelsoft.vo.VisitMcBean;
import org.eredlab.g4.rif.web.BaseAction;
import org.eredlab.g4.rif.web.CommonActionForm;

import com.google.gson.Gson;

public class VisitMobileAction extends BaseAction {

	private static Log log = LogFactory.getLog(VisitMobileAction.class);
	private VisitService visitService = (VisitService) super.getService("visitService");
	private int test_num = 0;
	
	public ActionForward UpdateStart(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String data_id = request.getParameter("data_id");
		String jsonString = request.getParameter("json");
		String gps_id = request.getParameter("gps_id");
		
		Dto dto = new BaseDto();
		dto.put("gps_id", gps_id);
		String enterprise_id = (String) g4Reader.queryForObject("Mobile.queryForEnterpriseId", dto);
		
		Gson gson = new Gson();
		VisitBean bean = null;
		try {
			bean = gson.fromJson(jsonString, VisitBean.class);
			
			Dto inDto = new BaseDto();
			inDto.put("data_id", data_id);
			inDto.put("gps_id", gps_id);
			inDto.put("enterprise_id", enterprise_id);
			inDto.put("dealer_id", bean.dealer_id);
			inDto.put("plan_id", bean.plan_id == null ? "":bean.plan_id);
			inDto.put("date1", bean.start_time);
			inDto.put("lon1", bean.start_lon);
			inDto.put("lat1", bean.start_lat);
			inDto.put("accuracy1", bean.start_accuracy);
			inDto.put("dealer_upload_status", bean.status);
			visitService.updateStart(inDto);
		} catch (Exception e) {
			e.printStackTrace();
			bean = new VisitBean();
		}
		Dto retDto = new BaseDto();
		retDto.put("result_code", "1");
		jsonString = JsonHelper.encodeObject2Json(retDto);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text;charset=utf-8");
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	public ActionForward UpdateArrive(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String data_id = request.getParameter("data_id");
		String jsonString = request.getParameter("json");
		
		Gson gson = new Gson();
		VisitBean bean = null;
		try {
			bean = gson.fromJson(jsonString, VisitBean.class);
			
			Dto inDto = new BaseDto();
			inDto.put("data_id", data_id);
			inDto.put("date2", bean.arrive_time);
			inDto.put("lon2", bean.arrive_lon);
			inDto.put("lat2", bean.arrive_lat);
			inDto.put("accuracy2", bean.arrive_accuracy);
			inDto.put("dealer_upload_status", bean.status);
			visitService.updateArrive(inDto);
		} catch (Exception e) {
			e.printStackTrace();
			bean = new VisitBean();
		}
		Dto retDto = new BaseDto();
		retDto.put("result_code", "1");
		jsonString = JsonHelper.encodeObject2Json(retDto);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text;charset=utf-8");
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	public ActionForward UpdateFinish(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String data_id = request.getParameter("data_id");
		String jsonString = request.getParameter("json");
		
		Gson gson = new Gson();
		VisitBean bean = null;
		try {
			bean = gson.fromJson(jsonString, VisitBean.class);
			
			Dto inDto = new BaseDto();
			inDto.put("data_id", data_id);
			inDto.put("date3", bean.service_begin_time);
			inDto.put("date4", bean.service_end_time);
			inDto.put("img_number", bean.visit_img_num);
			inDto.put("phone_time", bean.upload_date);
			inDto.put("upload_date", G4Utils.getCurrentTime());
			inDto.put("dealer_upload_status", bean.status);
			visitService.updateFinish(inDto);
		} catch (Exception e) {
			e.printStackTrace();
			bean = new VisitBean();
		}
		Dto retDto = new BaseDto();
		retDto.put("result_code", "1");
		jsonString = JsonHelper.encodeObject2Json(retDto);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text;charset=utf-8");
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	/**
	 * 带签名图片
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward UpdateEvaluate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("---------------------------");
		log.info("---------------------------");
		CommonActionForm aForm = (CommonActionForm) form;
		String data_id = request.getParameter("data_id");
		String jsonString = request.getParameter("json");
		
		Gson gson = new Gson();
		VisitEvaluateBean bean = null;
		try {
			bean = gson.fromJson(jsonString, VisitEvaluateBean.class);
			
			FormFile myFile = aForm.getFile1();
			String old_filename = "";
			String filename = "";
			if(myFile != null) {
				String uuid = UUIDGenerator.getUUID();
				old_filename = myFile.getFileName();
				log.info("评价签名接口：正在接收文件,文件名称=" + myFile.getFileName());
				filename = uploadFile(myFile, uuid, WzlConstants.IMG_SIGN);
				log.info("评价签名接口：文件接收成功,文件名称=" + filename);
			}
			
			Dto inDto = new BaseDto();
			inDto.put("data_id", data_id);
			inDto.put("visit_number", bean.visit_num);
			inDto.put("other_job", bean.other_job);
			inDto.put("proposal", bean.advise);
			inDto.put("evaluate_name", bean.service_name);
			inDto.put("evaluate_value", bean.service_value);
			inDto.put("signature_img_file", filename);
			inDto.put("signature_img_date", old_filename);
			visitService.updateEvaluate(inDto);
		} catch (Exception e) {
			e.printStackTrace();
			Dto retDto = new BaseDto();
			retDto.put("result_code", "99");
			jsonString = JsonHelper.encodeObject2Json(retDto);
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text;charset=utf-8");
			write(jsonString, response);
		}
		Dto retDto = new BaseDto();
		retDto.put("result_code", "1");
		jsonString = JsonHelper.encodeObject2Json(retDto);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text;charset=utf-8");
		write(jsonString, response);
		
		log.info("---------------------------");
		log.info("---------------------------");
		return mapping.findForward(null);
	}
	
	/**
	 * 带签名图片
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward UpdateMc(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("---------------------------");
		log.info("---------------------------");
		CommonActionForm aForm = (CommonActionForm) form;
		String data_id = request.getParameter("data_id");
		String jsonString = request.getParameter("json");
		
		Gson gson = new Gson();
		VisitMcBean bean = null;
		try {
			bean = gson.fromJson(jsonString, VisitMcBean.class);
			
			FormFile myFile = aForm.getFile1();
			String old_filename = "";
			String filename = "";
			if(myFile != null) {
				String uuid = UUIDGenerator.getUUID();
				old_filename = myFile.getFileName();
				log.info("机修签名接口：正在接收文件,文件名称=" + myFile.getFileName());
				filename = uploadFile(myFile, uuid, WzlConstants.IMG_SIGN);
				log.info("机修签名接口：文件接收成功,文件名称=" + filename);
			}
			
			test_num = Integer.parseInt(bean.upload_img_num);
			log.info("机修接口：data_id : "+data_id+", json : "+jsonString+", filename : "+filename);
//			Dto inDto = new BaseDto();
//			inDto.put("data_id", data_id);
//			inDto.put("visit_number", bean.visit_num);
//			inDto.put("other_job", bean.other_job);
//			inDto.put("proposal", bean.advise);
//			inDto.put("evaluate_name", bean.service_name);
//			inDto.put("evaluate_value", bean.service_value);
//			inDto.put("signature_img_file", filename);
//			inDto.put("signature_img_date", old_filename);
//			visitService.updateEvaluate(inDto);
		} catch (Exception e) {
			e.printStackTrace();
			Dto retDto = new BaseDto();
			retDto.put("result_code", "99");
			jsonString = JsonHelper.encodeObject2Json(retDto);
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text;charset=utf-8");
			write(jsonString, response);
		}
		Dto retDto = new BaseDto();
		retDto.put("result_code", "1");
		jsonString = JsonHelper.encodeObject2Json(retDto);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text;charset=utf-8");
		write(jsonString, response);
		
		log.info("---------------------------");
		log.info("---------------------------");
		return mapping.findForward(null);
	}
	
	/**
	 * 图片接收接口(新)
	 * 
	 * @param
	 * @return
	 * @throws IOException 
	 */
	public ActionForward ImgUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		log.info("---------------------------");
		log.info("---------------------------");
		log.info("图片接收接口:Start...");
		
		String data_id = request.getParameter("data_id");
		String jsonString = request.getParameter("json");
		//类型(1.拜访上传，2机修上传，3机修签名，4服务评价签名)
		String type = request.getParameter("type");
		
		log.info("图片接收接口：基本参数接收成功,data_id=" + data_id + ",type=" + type + "jsonString="+jsonString);
		
		Dto retDto = new BaseDto();
		Gson gson = new Gson();
		UploadImgBean bean;
		try {
			bean = gson.fromJson(jsonString, UploadImgBean.class);
			int count = (Integer)g4Reader.queryForObject("VisitMobile.checkUploadImg", bean.id);
			if (count > 0) {//repeat
				retDto.put("resultcode", "98");
				jsonString = JsonHelper.encodeObject2Json(retDto);
				response.setHeader("Cache-Control", "no-cache");
				response.setContentType("text;charset=utf-8");
				write(jsonString, response);
				return mapping.findForward(null);
			}
			
			//判断类型
			String typeName = "";
			if ("1".equals(type)) {
				typeName = WzlConstants.IMG_VISIT;
			}else if("2".equals(type)){
				typeName = WzlConstants.IMG_MC;
			}else if("3".equals(type)){
				typeName = WzlConstants.IMG_SIGN;
			}else if("4".equals(type)){
				typeName = WzlConstants.IMG_SIGN;
			}else{
				//若type为其他值，返回错误
				retDto.put("resultcode", "99");
				jsonString = JsonHelper.encodeObject2Json(retDto);
				response.setHeader("Cache-Control", "no-cache");
				response.setContentType("text;charset=utf-8");
				write(jsonString, response);
				return mapping.findForward(null);
			}
			//文件接收处理
			String filename = "";
			MultipartRequestHandler multipartRequestHandler = form.getMultipartRequestHandler();
			Hashtable elements = multipartRequestHandler.getFileElements();
			Collection values = elements.values();
			Iterator iterator = values.iterator();
			while(iterator.hasNext()){
				FormFile myFile = (FormFile)iterator.next();
				if(myFile != null){
					String uuid = UUIDGenerator.getUUID();
					log.info("图片接收接口：正在接收文件,type=" + type + ",文件名称=" + myFile.getFileName());
					filename = uploadFile(myFile, uuid, typeName);
					log.info("图片接收接口：文件接收成功,type=" + type + ",文件名称=" + filename);
				}
			}
			
			String now = G4Utils.getCurrentTime();
			Dto u_dto = new BaseDto();
			u_dto.put("id", bean.id);
			u_dto.put("data_id", data_id);
			u_dto.put("type", bean.type);
			u_dto.put("img_path", filename);
			u_dto.put("img_name", filename);
			u_dto.put("upload_date", now);
			visitService.insertUploadImg(u_dto);
			
			if ("1".equals(type)) {
				String old_file = (String)g4Reader.queryForObject("VisitMobile.queryImgType1", data_id);
				String new_file = old_file + "|" + filename;
				Dto inDto = new BaseDto();
				inDto.put("data_id", data_id);
				inDto.put("img_file", new_file);
				visitService.updateImgType1(inDto);
				String img_num = (String)g4Reader.queryForObject("VisitMobile.queryImgNumType1", data_id);
				retDto.put("img_num", img_num);
			}else if("2".equals(type)){
				test_num = test_num - 1;
				retDto.put("img_num", test_num);
			}else if("3".equals(type)){
			}else if("4".equals(type)){
				Dto inDto = new BaseDto();
				inDto.put("signature_img_file", filename);
				inDto.put("signature_img_date", now);
				visitService.updateEvaluateSign(inDto);
			}
			
			retDto.put("result_code", "1");
			jsonString = JsonHelper.encodeObject2Json(retDto);
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text;charset=utf-8");
			write(jsonString, response);
		} catch (Exception e) {
			log.info("图片接收接口：基本参数接收异常,errMsg="+e.getMessage());
			retDto.put("resultcode", "99");
			jsonString = JsonHelper.encodeObject2Json(retDto);
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text;charset=utf-8");
			write(jsonString, response);
			e.printStackTrace();
		}
		log.info("---------------------------");
		log.info("---------------------------");
		return mapping.findForward(null);
	}
	
	/**
	 * 文件上传
	 * @param gpsId
	 * @param pin
	 * @return 
	 */
	private String uploadFile(FormFile myFile,String data_id,String type) throws Exception{
		String savePath = getServlet().getServletContext().getRealPath("/") + "uploaddata/";
		String path =  type + "/" + G4Utils.getCurDate() + "/";
		// 检查路径是否存在,如果不存在则创建之
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdir();
		}
		
		// 检查路径是否存在,如果不存在则创建之
		File file0 = new File(savePath+type+ "/");
		if (!file0.exists()) {
			file0.mkdir();
		}
		
		// 文件按天归档
		savePath = savePath + path;
		File file1 = new File(savePath);
		if (!file1.exists()) {
			file1.mkdir();
		}
		// 我们一般会根据某种命名规则对其进行重命名,截取文件后缀名
		int start = myFile.getFileName().lastIndexOf(".");
		String fileName = data_id+myFile.getFileName().substring(start);
		File fileToCreate = new File(savePath, fileName);
		// 检查同名文件是否存在,不存在则将文件流写入文件磁盘系统
		if (!fileToCreate.exists()) {
			FileOutputStream os = new FileOutputStream(fileToCreate);
			os.write(myFile.getFileData());
			os.flush();
			os.close();
		} else {
			// 此路径下已存在同名文件,是否要覆盖或给客户端提示信息由你自己决定
			FileOutputStream os = new FileOutputStream(fileToCreate);
			os.write(myFile.getFileData());
			os.flush();
			os.close();
		}
		
		//若上传的为视频，直接返回，无需生成缩略图
		if(fileName.contains("3gp") || fileName.contains("mov")){
			log.info("视频上传中,上传成功!");
			return path + fileName;
		}
		
		//若上传的为音频，直接返回
		if(fileName.contains("amr")){
			log.info("音频上传中,上传成功!");
			return path + fileName;
		}
		
		//生成缩略图
		imgZoom(savePath,fileName);
		log.info("图片上传中,上传成功!");
		return path + fileName;
	}
	
	/**
	 * 生成缩略图
	 * @param filePath
	 */
	private void imgZoom(String path, String fileName) throws Exception{
		String nFileName = fileName.replace("jpg", "png");
		Thumbnails.of(path+fileName)
					.size(100, 100)
					.keepAspectRatio(true)
					.toFile(path+"small_"+nFileName);
	}
}
