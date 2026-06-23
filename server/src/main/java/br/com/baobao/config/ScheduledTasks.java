package br.com.baobao.config;

import br.com.baobao.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private ProdutoService produtoService;

    // Executar diariamente às 2 da manhã
    @Scheduled(cron = "0 0 2 * * *")
    public void limparImagensOrfas() {
        produtoService.limparImagensOrfas();
    }
}