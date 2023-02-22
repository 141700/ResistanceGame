package resistanceGame.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import resistanceGame.ResistanceGameApplication;
import resistanceGame.form.MainForm;
import resistanceGame.service.GameService;
import resistanceGame.service.RequestService;

import java.util.Collections;

@RequiredArgsConstructor
@Controller
@RequestMapping
public class GameplayController {

    @Autowired
    GameService gameService;

    @Autowired
    RequestService requestService;


    @GetMapping("/")
    public String showHomeForm(MainForm form, Model model) {
        model.addAttribute("opened", gameService.getOpenGamesList());
        return "home";
    }

    @PostMapping("/")
    public String handleInitaialSubmit(MainForm form, Model model) {
        if (!form.isNameValid()) {
            model.addAttribute("nameerror", true);
            return showHomeForm(form, model);
        }
        form.setUuid(requestService.initGame(form.getName(), form.getJoinedGameId()));
        return handleSessionSubmit(form, model);
    }

    @PostMapping("/session")
    public String handleSessionSubmit(MainForm form, Model model) {
        model.addAttribute("gameplay", requestService.handleIncomeRequest(form));
        return "match";
    }

    @GetMapping("/restartapp")
    public String handleRestartRequest(MainForm form, Model model) {
        ResistanceGameApplication.restart();
        model.addAttribute("opened", Collections.EMPTY_MAP);
        return "home";
    }
}

