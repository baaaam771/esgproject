package program.textboard;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import program.common.CamelMap;
import program.common.DataMap;
import program.common.service.CommonService;
import program.common.util.HttpUtil;
import program.security.Account;
import program.textboard.mapper.TextBoardMapper;

@Controller
@RequestMapping(value = { "board" })
public class TextBoardController {

	private static final Logger logger = LoggerFactory.getLogger(TextBoardController.class);

	@Autowired
	private TextBoardMapper textBoardMapper;
	@Autowired
	private CommonService commonService;

	/**************************************************
	 * @MethodName : textbordList
	 * @Description: 뿜업 게시판 리스트
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 5.
	 **************************************************/
	@RequestMapping(value = { "/textboardList" })
	public String textboardList(Model model) {
		return "contents/board/textboardList";
	}

	/**************************************************
	 * @MethodName : textboardView
	 * @Description: 뿜업 게시판 상세
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/textboardView" })
	public String textboardView(HttpServletRequest request,Model model) {
	
		DataMap paramMap = HttpUtil.getRequestDataMap(request);
		HttpUtil.getParams(paramMap, model);
		
		return "contents/board/textboardView";
	}

	/**************************************************
	 * @MethodName : textboardWrite
	 * @Description: 뿜업 게시판 작성
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/textboardWrite" })
	public String textboardWrite(Model model) {
		return "contents/board/textboardWrite";
	}

	/**************************************************
	 * @MethodName : noticeList
	 * @Description: 공지사항 리스트
	 * @param model
	 * @return String
	 * @Author : Hyung-Seon. Yoon
	 * @Version : 2021. 7. 6.
	 **************************************************/
	@RequestMapping(value = { "/noticeList" })
	public String noticeList(Model model) {
		return "contents/board/noticeList";
	}

	
	/**************************************************
	* @MethodName : noticeView
	* @Description: 공지사항 상세
	* @param request
	* @param model
	* @return String
	* @Author : Ye-Jin. Jeong
	* @Version : 2021. 8. 26.
	**************************************************/
	@RequestMapping(value = { "/noticeView" })
	public String noticeView(HttpServletRequest request,Model model) {
		
		DataMap paramMap = HttpUtil.getRequestDataMap(request);
		HttpUtil.getParams(paramMap, model);
		
		return "contents/board/noticeView";
	}


	/**************************************************
	* @MethodName : posting
	* @Description: 뿜업게시판 글 등록
	* @param model
	* @param request
	* @return Boolean
	* @Author : Ye-Jin. Jeong
	* @Version : 2021. 8. 25.
	**************************************************/
	@ResponseBody
	@SuppressWarnings("unchecked")
	@RequestMapping(value= {"/posting"},method= {RequestMethod.GET,RequestMethod.POST})
	public Boolean posting(Model model, HttpServletRequest request) {
		logger.debug("TextBoardController : posting - start");
		
		DataMap paramMap=HttpUtil.getRequestDataMap(request);
		
		int rst = 0;
		
		paramMap.put("filePath", "BBOOM_BOARD");
		List<CamelMap> rstFileList = null;

		try {
			rstFileList = commonService.saveFile(request, paramMap);
			for (CamelMap fileMap : rstFileList) {
				logger.info("fileMap : {}", fileMap);
				paramMap.put("attFile", fileMap.get("saveFilePath") + "/" + fileMap.get("saveFileName"));
			}
			rst = textBoardMapper.posting(paramMap);
		} catch (Exception e) {
			logger.debug("게시글 등록 오류", e);
		}

		logger.debug("TextBoardController : posting - end");
		return rst>0 ? true : false;
	}

	/**************************************************
	 * @MethodName : getBoardList
	 * @Description: 게시글 리스트 조회 컨트롤러
	 * @param request
	 * @param model
	 * @return ModelAndView
	 * @Author : Ye-Jin. Jeong
	 * @Version : 2021. 8. 13.
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
		System.out.println(paramMap.toString());
		CamelMap resultInfo = null;
		
		//TODO : 사용자 로그인 세션
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication.getPrincipal() == "anonymousUser") {
	       logger.debug("상담 상세 페이지 조회: 로그인하지 않은 상태");
	    }else {
	         Account account = (Account)authentication.getPrincipal();
	         paramMap.put("uName", account.getUsername()); 
	    }
	    
		try {
			resultInfo = textBoardMapper.getBoardInfo(paramMap);
		} catch (Exception e) {
			logger.debug("게시글 조회 오류", e);
		}

		mv.addObject("resultInfo", resultInfo);
		mv.addObject("userInfo", paramMap.getString("uName"));

		logger.debug("TextBoardController : getBoardInfo - end");
		return mv;
	}
	
	/************************************************** Notice **************************************************/

	/**************************************************
	* @MethodName : getNoticeList
	* @Description: 공지사항 리스트 조회 컨트롤러
	* @param request
	* @param model
	* @return ModelAndView
	* @Author : Ye-Jin. Jeong
	* @Version : 2021. 8. 24.
	**************************************************/
	@ResponseBody
	@RequestMapping(value = { "/getNoticeList" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getNoticeList(HttpServletRequest request, Model model) {
		logger.debug("TextBoardController : getNoticeList - start");

		ModelAndView mv = new ModelAndView("jsonView");

		List<CamelMap> resultList = null;

		try {
			resultList = textBoardMapper.getNoticeList();
		} catch (Exception e) {
			logger.debug("게시글 리스트 조회 오류", e);
		}

		mv.addObject("resultList", resultList);

		logger.debug("TextBoardController : getNoticeList - end");
		return mv;
	}
	/**************************************************
	* @MethodName : getNoticeInfo
	* @Description: 공지사항 상세 조회 컨트롤러
	* @param request
	* @param model
	* @return ModelAndView
	* @Author : Ye-Jin. Jeong
	* @Version : 2021. 8. 24.
	**************************************************/
	@ResponseBody
	@RequestMapping(value = { "/getNoticeInfo" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getNoticeInfo(HttpServletRequest request, Model model) {
		logger.debug("TextBoardController : getNoticeInfo - start");

		ModelAndView mv = new ModelAndView("jsonView");

		DataMap paramMap = HttpUtil.getRequestDataMap(request);
		
		System.out.println(paramMap.toString());
		CamelMap resultInfo = null;

		try {
			resultInfo = textBoardMapper.getNoticeInfo(paramMap);
		} catch (Exception e) {
			logger.debug("게시글 조회 오류", e);
		}

		mv.addObject("resultInfo", resultInfo);

		logger.debug("TextBoardController : getNoticeInfo - end");
		return mv;
	}
	/**************************************************
	* @MethodName : getNoticePrev
	* @Description: 이전 게시글 조회
	* @param request
	* @param model
	* @return ModelAndView
	* @Author : Ye-Jin. Jeong
	* @Version : 2021. 8. 24.
	**************************************************/
	@ResponseBody
	@RequestMapping(value = { "/getNoticePrev" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getNoticePrev(HttpServletRequest request, Model model) {
		logger.debug("TextBoardController : getNoticePrev - start");

		ModelAndView mv = new ModelAndView("jsonView");

		DataMap paramMap = HttpUtil.getRequestDataMap(request);
		
		System.out.println(paramMap.toString());
		CamelMap resultInfo = null;

		try {
			resultInfo = textBoardMapper.getNoticePrev(paramMap);
		} catch (Exception e) {
			logger.debug("게시글 조회 오류", e);
		}

		mv.addObject("resultInfo", resultInfo);

		logger.debug("TextBoardController : getNoticePrev - end");
		return mv;
	}
	/**************************************************
	* @MethodName : getNoticeNext
	* @Description: 다음 게시글 조회
	* @param request
	* @param model
	* @return ModelAndView
	* @Author : Ye-Jin. Jeong
	* @Version : 2021. 8. 24.
	**************************************************/
	@ResponseBody
	@RequestMapping(value = { "/getNoticeNext" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getNoticeNext(HttpServletRequest request, Model model) {
		logger.debug("TextBoardController : getNoticeNext - start");

		ModelAndView mv = new ModelAndView("jsonView");

		DataMap paramMap = HttpUtil.getRequestDataMap(request);
		
		System.out.println(paramMap.toString());
		CamelMap resultInfo = null;

		try {
			resultInfo = textBoardMapper.getNoticeNext(paramMap);
		} catch (Exception e) {
			logger.debug("게시글 조회 오류", e);
		}

		mv.addObject("resultInfo", resultInfo);

		logger.debug("TextBoardController : getNoticeNext - end");
		return mv;
	}
}
