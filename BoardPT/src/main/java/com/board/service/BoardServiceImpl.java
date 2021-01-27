package com.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.dto.BbsFileDto;
import com.board.dto.BoardDto;
import com.board.dto.CommentDto;
import com.board.mapper.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService {
	
	private Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);

	@Autowired
	private BoardMapper boardMapper;
	
	@Override
	public int maxNumBoard() {
		return boardMapper.maxNumBoard();
	}

	@Override
	public int insertBoard(BoardDto boardVO) {
		return boardMapper.insertBoard(boardVO);
	}

	@Override
	public Map<String, Object> getBoards(Map<String, Integer> pageMap, Map<String, String> paramMap) {
		log.info("<< BoardServiceImpl // getBoards >>");
		Map<String, Object> result = new HashMap<>();
		
		int pageSize 		= pageMap.get("pageSize");					// 한 페이지의 게시글 개수
		int pageBlockSize 	= pageMap.get("pageBlockSize");		// 한 페이지 블록을 구성하는 페이지 개수
//		int pageSizeHit 	= pageMap.get("pageSizeHit");			// 조회순 정렬 게시글 페이지 노출 개수
		
		String strPageNum 	= (String) paramMap.get("pageNum");	// 시작 페이지 번호
		String search 		= (String) paramMap.get("search");		// 검색어
		String category 	= (String) paramMap.get("category");
		
		strPageNum 	= (strPageNum == null | "".equals(strPageNum)) ? "1" : strPageNum;	// 시작 페이지 기본값 1
		int pageNum = Integer.parseInt(strPageNum); 	// 페이지 번호
		pageNum		= (pageNum <= 0) ? 1 : pageNum;
		log.info("페이지 번호//" + strPageNum);
		
		int startRow = (pageNum - 1) * pageSize; 		// 페이지의 시작 행 번호
		
		List<BoardDto> boardList = boardMapper.getBoards(search, category, pageSize, startRow);
		
		int allRowCount = boardMapper.getBoardCount(search, category);		// 화면 표시되는 모든 게시글 개수
		
		int maxPage 	= allRowCount / pageSize + ((allRowCount % pageSize > 0) ? 1 : 0); // 페이지 블록 구하기
		int startPage 	= (pageNum / pageBlockSize - ((pageNum % pageBlockSize == 0) ? 1 : 0)) * pageBlockSize + 1;
		int endPage 	= startPage + pageBlockSize - 1;
		endPage 		= (endPage > maxPage) ? maxPage : endPage;
		
		Map<String, Integer> pageInfoMap = new HashMap<>();			// 페이지 관련 데이터
		pageInfoMap.put("startPage"		, startPage);
		pageInfoMap.put("endPage"		, endPage);
		pageInfoMap.put("pageBlockSize"	, pageBlockSize);
		pageInfoMap.put("maxPage"		, maxPage);
		pageInfoMap.put("allRowCount"	, allRowCount);
		pageInfoMap.put("pageNum"		, pageNum);
		
		result.put("pageInfoMap"	, pageInfoMap);		// 페이지 관련 데이터
		result.put("boardList"		, boardList);			// 페이지 게시글 리스트
		
		return result;
	}

	@Override
	public List<BoardDto> getBoardsMain(int pageSize, int startRow) {
		return boardMapper.getBoardsMain(pageSize, startRow);
	}

	@Override
	public List<BoardDto> getBoardsOrderByHit(int pageSizeHit) {
		return boardMapper.getBoardsOrderByHit(pageSizeHit);
	}

	@Override
	public int getBoardCount(String search, String category) {
		return boardMapper.getBoardCount(search, category);
	}
	
//	@Transactional
	@Override
	public Map<String, Object> getBoardByNum(int num, String sessionID) {
		Map<String, Object> resultMap = new HashMap<>();
		BoardDto resultBoard = boardMapper.getBoardByNum(num); 
		log.info("num, sessionID : {}, {}", num, sessionID);
		if (!resultBoard.getId().equals(sessionID)) {
			boardMapper.updateHit(num);
		}
		
		List<CommentDto> commentList	= boardMapper.getCommentByBoardNum(num);
		log.info("comment : {}", commentList.toString());
		
		List<BbsFileDto> fileList	= boardMapper.selectFileByBoardNum(num);
		
		resultMap.put("resultBoard"	, resultBoard);
		resultMap.put("commentList"	, commentList);
		resultMap.put("fileList"	, fileList);
		
		return resultMap;
	}

	
	@Override
	public List<CommentDto> getCommentByBoardNum(int num) {
		return boardMapper.getCommentByBoardNum(num);
	}
	

	@Override
	public int updateBoard(BoardDto boardVO) {
		return boardMapper.updateBoard(boardVO);
	}

	@Transactional
	@Override
	public int deleteBoard(int num) {
		int countDeleteBoard	= boardMapper.deleteBoard(num);
		int countFile 			= boardMapper.getCountFileByNum(num);
		int c2 					= boardMapper.deleteFileByNum(num);
		System.out.println("countFile/" + countFile + "/" + c2);
		
		int countComment 	= boardMapper.getCountCommentByNum(num);
		int c3 				= boardMapper.deleteCommentByNum(num);
		System.out.println("countComment/" + countComment + "/" + c3);
		
		return countDeleteBoard;

	}

	
	/* Comment */
	
	@Transactional
	@Override
	public void insertComment(CommentDto comment) {
		log.info("[ Insert Comment  ]");
		boardMapper.insertComment(comment);
		boardMapper.commentCount(comment.getNum());
		
	}
	
	@Transactional
	@Override
	public void deleteComment(int commentId) {
		log.info("[ Delete Comment ] {}", commentId);
		int num = boardMapper.getNumByCommentId(commentId);
		boardMapper.deleteComment(commentId);
		boardMapper.commentCount(num);
		
	}

	
	/* Comment */
	

	
	
	
	/* File */
	
	public BbsFileDto selectFileByFileId(String fileId) {
		BbsFileDto file = boardMapper.selectFileByFileId(fileId);
		
		return file;
	}
	
	
	
	
	
	
}
