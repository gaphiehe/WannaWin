package citiMerchant.strategy;

import citiMerchant.mapper.StrategyMapper;
import citiMerchant.vo.Strategy;
import citiMerchant.vo.StrategyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhong on 2018/7/11 19:52
 * @author 彭璇
 */
@Controller
public class StrategyController {
    @Autowired StrategyService strategyService;


    @RequestMapping("/strategy/getStrategyList")
    public ModelAndView getStrategyList(String merchantID){
        ModelAndView mv = new ModelAndView();
        List<StrategyDAO> strategies = strategyService.getStrategyList(merchantID);
        if(strategies==null)
            mv.addObject("strategies",new ArrayList<StrategyDAO>());
        else
            mv.addObject("strategies",strategies);
        mv.setViewName("strategy/strategyList");
        return mv;
    }

    @RequestMapping("/strategy/deleteStrategy")
    public ModelAndView deleteStrategy(String merchantID,String strategyID){
        ModelAndView mv = new ModelAndView();
        strategyService.deleteStrategy(strategyID);
        List<StrategyDAO> strategies = strategyService.getStrategyList(merchantID);
        if(strategies==null)
            mv.addObject("strategies",new ArrayList<StrategyDAO>());
        else
            mv.addObject("strategies",strategies);
        mv.setViewName("strategy/strategyList");
        return mv;
    }

    @RequestMapping("/strategy/editStrategyRequest")
    public ModelAndView editStrategyRequest(String strategyID){
        ModelAndView mv = new ModelAndView();
        StrategyDAO strategyDAO = strategyService.editStrategyRequest(strategyID);
        mv.addObject("strategy",strategyDAO);
        mv.setViewName("strategy/editStrategy");
        return mv;
    }

    @RequestMapping("/strategy/editStrategySubmit")
    public ModelAndView editStrategySubmit(StrategyDAO strategyDAO){
        ModelAndView mv = new ModelAndView();
        strategyService.editStrategySubmit(strategyDAO);
        mv.addObject("strategies",strategyService.getStrategyList(strategyDAO.getMerchantID()));
        mv.setViewName("strategy/getStrategyList");
        return mv;
    }

    @RequestMapping("/strategy/addStrategyRequest")
    public ModelAndView addStrategyRequest(String merchantID){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/strategy/addStrategy");
        mv.addObject("merchantID",merchantID);
        return mv;
    }

    @RequestMapping("/strategy/addStrategySubmit")
    public ModelAndView addStrategySubmit(String merchantID, int full,int discount,int points){
        ModelAndView mv = new ModelAndView();
        StrategyDAO strategyDAO = new StrategyDAO(UUID.randomUUID().toString(),merchantID,full,discount,points);
        strategyService.addStrategy(strategyDAO);

        List<StrategyDAO> strategies = strategyService.getStrategyList(merchantID);
        mv.addObject("strategies",strategies);
        mv.setViewName("strategy/strategyList");
        return mv;
    }


}
