package ebrainsoft.week3.controller;

import ebrainsoft.model.BoardInfo;
import ebrainsoft.model.FilterCondition;
import ebrainsoft.model.IndexServiceInfo;
import ebrainsoft.util.SearchUtil;
import ebrainsoft.week3.service.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/week3")
public class IndexController {
    private final IndexService indexService;

    @GetMapping("/index")
    public String getBoardList(HttpServletRequest request, Model model) throws Exception {
        FilterCondition fc = SearchUtil.updateFilterConditionIfSearchConditionExist(request);

        IndexServiceInfo indexServiceInfo = indexService.getIndexInfo(fc);

        modelUpdate(model, indexServiceInfo, fc);

        request.getSession().setAttribute("curPage", indexServiceInfo.getBoardInfo().getNeedPageNum());

        return "index";
    }

    private void modelUpdate(Model model, IndexServiceInfo indexServiceInfo, FilterCondition fc) {
        BoardInfo boardInfo = indexServiceInfo.getBoardInfo();
        model.addAttribute("categoryList", indexServiceInfo.getCategoryList());
        model.addAttribute("fc", fc);
        model.addAttribute("boardList", boardInfo.getBoardList());
        model.addAttribute("totalCount", boardInfo.getTotalCount());

        model.addAttribute("curPage", boardInfo.getNeedPageNum());
        model.addAttribute("totalPage", boardInfo.getTotalPage());
        model.addAttribute("prevPage", boardInfo.getPrevPage());
        model.addAttribute("nextPage", boardInfo.getNextPage());

        model.addAttribute("pageLimitStart", boardInfo.getPageLimitStart());
        model.addAttribute("pageLimitEnd", boardInfo.getPageLimitEnd(boardInfo.getPageLimitStart()));
    }
}
