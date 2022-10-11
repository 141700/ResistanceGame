package resistanceGame.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import resistanceGame.form.MainForm;
import resistanceGame.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import resistanceGame.service.GameplayService;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping
@Log4j2
@Validated
public class GameplayController {

    @Autowired
    GameService gameService;

    @Autowired
    GameplayService gameplayService;

    @GetMapping("/")
    public String showHomeForm(MainForm form, Model model) { // без формы падает!
        model.addAttribute("opened", gameService.getOpenGamesList());
        return "home";
    }

    @PostMapping("/")
//    public String handleInitaialSubmit(@ModelAttribute("mainForm") MainForm form, Model model) {
    public String handleInitaialSubmit(MainForm form, Model model) {
        if (!form.isNameValid()) {
            model.addAttribute("nameerror", true);
            return showHomeForm(form, model);
        }
        int gameId = form.getJoinedGameId() != 0 ? form.getJoinedGameId() : gameService.createGame();
        form.setUuid(gameService.createAndAddPlayerToGame(gameId, form.getName()));
        return handleSessionSubmit(form, model);
    }

    @PostMapping("/session")
    public String handleSessionSubmit(MainForm form, Model model) {  // @RequestParam UUID uuid тоже работает из формы!!!
        model.addAttribute("gameplay", gameplayService.analyzeUserRequest(form));
        return "match";
    }
}

