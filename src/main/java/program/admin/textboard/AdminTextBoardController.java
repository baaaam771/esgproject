package program.admin.textboard;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import program.admin.textboard.mapper.AdminTextBoardMapper;
import program.common.CamelMap;
import program.common.DataMap;
import program.common.service.CommonService;
import program.common.util.HttpUtil;
import program.common.util.StringUtil;
import program.textboard.TextBoardController;
import program.textboard.mapper.TextBoardMapper;

/**************************************************
 * @FileName : AdminTextBoardController.java
 * @Description: 관리자 페이지 뿜업 게시판 컨트롤러
 * @Author : Hyung-Seon. Yoon
 * @Version : 2021. 7. 5.
 * @Copyright : ⓒADUP. All Right Reserved
 **************************************************/
@Controller
@RequestMapping(value = { "/admin/textboard" })
public class AdminTextBoardController {
	private static final Logger logger = LoggerFactory.getLogger(TextBoardController.class);

	@Autowired
	private CommonService commonService;
	@Autowired
	private TextBoardMapper textBoardMapper;
	@Autowired
	private AdminTextBoardMapper adminTBMapper;
	
//	private TextBoardService textBoardService;

	/**************************************************
	 * @MethodName : textboardList
	 * @Description: 관리자 페이지 뿜업 게시판 리스트
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 5.
	 **************************************************/
	@RequestMapping(value = { "/textboardList" })
	public String textboardList(Model model) {
		return "/admin/textboard/textboardList";
	}

	/**************************************************
	 * @MethodName : textboardView
	 * @Description: 관리자 페이지 뿜업 게시판 상세
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/textboardView" })
	public String textboardView(Model model) {
		return "/admin/textboard/textboardView";
	}

	/**************************************************
	 * @MethodName : textboardWrite
	 * @Description: 관리자 페이지 뿜업 게시판 작성
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/textboardWrite" })
	public String textboardWrite(Model model) {
		return "/admin/textboard/textboardWrite";
	}

	/**************************************************
	 * @MethodName : noticeList
	 * @Description: 관리자 페이지 공지사항 리스트
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/noticeList" })
	public String noticeList(Model model) {
		return "/admin/textboard/noticeList";
	}

	/**************************************************
	 * @MethodName : noticeView
	 * @Description: 관리자 페이지 공지사항 상세
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/noticeView" })
	public String noticeView(Model model) {
		return "/admin/textboard/noticeView";
	}

	/**************************************************
	 * @MethodName : noticeWrite
	 * @Description: 관리자 페이지 공지사항 작성
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/noticeWrite" })
	public String noticeWrite(Model model) {
		return "/admin/textboard/noticeWrite";
	}

	/**************************************************
	 * @MethodName : ajaxSiteBoardSave
	 * @Description:
	 * @param request
	 * @param model
	 * @return boolean
	 * @Author : Ye-Jin. Jeong
	 * @Version : 2021. 8. 17.
	 **************************************************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/ajaxSiteBoardSave" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public boolean ajaxSiteBoardSave(HttpServletRequest request, Model model) {
		logger.debug("AdminTextBoardController : ajaxSiteBoardSave - start");
		
		DataMap paramMap = HttpUtil.getRequestDataMap(request);

		boolean sw = false;

		// Authentication authentication =
		// SecurityContextHolder.getContext().getAuthentication();
		// String adminId = authentication.getName();
		//String mode = paramMap.getString("mode");
		//String seq = paramMap.getString("seq");

		// paramMap.put("adminId", adminId);

		paramMap.put("filePath", "SITE_BOARD");
		List<CamelMap> rstFileList = null;

		try {
			rstFileList = commonService.saveFile(request, paramMap);
			for (CamelMap fileMap : rstFileList) {
				logger.info("fileMap : {}", fileMap);
				paramMap.put("attFile", fileMap.get("saveFilePath") + "/" + fileMap.get("saveFileName"));
			}

			//if ("MOD".equals(mode) && !StringUtil.isEmpty(seq)) {

				if (!StringUtil.isEmpty(paramMap.getString("attFile"))) {
					paramMap.put("fileName", paramMap.getString("orgAttFile"));
					commonService.deleteFile(paramMap);
				}
				// sw = adminTBMapper.updateSiteBoard(paramMap);
			//} else {
				sw = adminTBMapper.insertSiteBoard(paramMap);
			//}

		} catch (Exception e) {
			logger.debug("사이트 공지사항 저장 오류", e);
		}
		logger.debug("AdminTextBoardController : ajaxSiteBoardSave - start");
		return sw;
	}

	/**************************************************
	 * @MethodName : getBoardList
	 * @Description: 게시글 리스트 조회 컨트롤러
	 * @param request
	 * @param model
	 * @return ModelAndView
	 * @Author : Ye-Jin. Jeong
	 * @Version : 2021. 8. 14.
	 **************************************************/
	@ResponseBody
	@RequestMapping(value = { "/getBoardList" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getBoardList(HttpServletRequest request, Model model) {
		logger.debug("TextBoardController : getBoardList - start");

		ModelAndView mv = new ModelAndView("jsonView");

		List<CamelMap> resultList = null;

		try {
			resultList = textBoardMapper.getBoardList();
		} catch (Exception e) {
			logger.debug("게시글 리스트 조회 오류", e);
		}

		mv.addObject("resultList", resultList);

		logger.debug("TextBoardController : getBoardList - end");
		return mv;
	}

	/**************************************************
	 * @MethodName : getBoardInfo
	 * @Description: 게시글 상세 조회 컨트롤러
	 * @param request
	 * @param model
	 * @return ModelAndView
	 * @Author : Ye-Jin. Jeong
	 * @Version : 2021. 8. 14.
	 **************************************************/
	@ResponseBody
	@RequestMapping(value = { "/getBoardInfo" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getBoardInfo(HttpServletRequest request, Model model) {
		logger.debug("TextBoardController : getBoardInfo - start");

		ModelAndView mv = new ModelAndView("jsonView");

		DataMap paramMap = HttpUtil.getRequestDataMap(request);
		CamelMap resultInfo = null;

		try {
			resultInfo = textBoardMapper.getBoardInfo(paramMap);
		} catch (Exception e) {
			logger.debug("게시글 조회 오류", e);
		}

		mv.addObject("resultInfo", resultInfo);

		logger.debug("TextBoardController : getBoardInfo - end");
		return mv;
	}
}
