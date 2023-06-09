package org.zerock.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/board/*")
@AllArgsConstructor
public class BoardController {
	
	private BoardService service; // ★@AllArgsConstructor 가 BoardService 자동주입!!
	
	// Home
	@GetMapping("")
	public void basic() {}
	
	// 목록보기 
	@GetMapping("/list")
	public void list(Criteria cri, Model model) {
		
		log.info("list : " + cri);
		
		model.addAttribute("list", service.getList(cri));
		
		int total = service.getTotal(cri); // 총 글의 갯수 구하기
		
		log.info("total : " + total);
		
		model.addAttribute("pageMaker", new PageDTO(total, cri));

	}
	
	// 등록하기 - get
	@GetMapping("/register")
	@PreAuthorize("isAuthenticated()") 
	public void register() {}
	
	// 등록하기 - post
	@PostMapping("/register") 
	@PreAuthorize("isAuthenticated()")
	public String register(BoardVO board, RedirectAttributes rttr) {
		
		log.info("register: " + board);
		
		service.register(board);
		
		rttr.addFlashAttribute("result", board.getBno());
		
		return "redirect:/board/list"; 
	}
	
	// 상세보기
	@GetMapping("/get")
	public void get(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
		
		log.info("/get");
		model.addAttribute("board", service.get(bno));

	}
	
	// 수정하기
	@GetMapping("/modify")
	@PreAuthorize("isAuthenticated()") 
	public void modify(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
		
		log.info("/modify");
		model.addAttribute("board", service.get(bno));

	}
	
	// 수정하기
	@PostMapping("/modify")
	@PreAuthorize("principal.username == #board.writer") 
	public String modify(BoardVO board, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
										
		log.info("modify: " + board);
		
		if(service.modify(board)) {
			rttr.addFlashAttribute("result", "success");
		} 
		
		rttr.addAttribute("bno", board.getBno());
		rttr.addAttribute("pageNum", cri.getPageNum());
		rttr.addAttribute("amount", cri.getAmount());
		rttr.addAttribute("type", cri.getType());
		rttr.addAttribute("keyword", cri.getKeyword());
		
		return "redirect:/board/get";
	}
	
	// 삭제하기
	@PostMapping("/remove") 
	@PreAuthorize("principal.username == #writer") 
	public String remove(@RequestParam("bno") Long bno, Criteria cri, RedirectAttributes rttr, String writer) {
		
		log.info("remove..." + bno);
		
		List<BoardAttachVO> attachList = service.getAttachList(bno); 
		
		if(service.remove(bno)) { 
			
			deleteFiles(attachList);
			
			rttr.addFlashAttribute("result", "success");
		}		
		
		return "redirect:/board/list" + cri.getListLink(); 
	}
	
	// 삭제하기 - 첨부파일 지우는 메소드
	private void deleteFiles(List<BoardAttachVO> attachList) {
		
		if(attachList == null || attachList.size() == 0) { 
			return;
		}
		
	    log.info("delete attach files...................");
	    log.info(attachList);
	    
	    attachList.forEach(attach -> { 
	    	try {
	    		Path file = Paths.get("C:\\upload\\" + attach.getUploadPath() + "\\" + attach.getUuid() + "_" + attach.getFileName()); 
	    		
	    		Files.deleteIfExists(file); 
	    		
	    		if(Files.probeContentType(file).startsWith("image")) { 
	    			Path thumbNail = Paths.get("C:\\upload\\" + attach.getUploadPath() + "\\s_" + attach.getUuid() + "_" + attach.getFileName());
	    			
	    			Files.delete(thumbNail); 
	    		}
	    	} catch (Exception e) {
	    		log.error("delete file error: " + e.getMessage());
	    	} // end catch
	    }); // end forEach
	} // end deleteFiles
	
	// 상세보기 - 첨부파일 목록
	@GetMapping(value = "/getAttachList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno) {
		
		log.info("get Attach List: " + service.getAttachList(bno));
		
		return new ResponseEntity<>(service.getAttachList(bno), HttpStatus.OK);
	}
	
}
