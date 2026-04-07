package com.quicksell.engine.bot;

import com.quicksell.engine.catalog.CatalogService;
import com.quicksell.engine.shop.Shop;
import com.quicksell.engine.shop.ShopRepository;
import com.quicksell.engine.shop.ShopStatus;
import jakarta.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class BotRegistryService {

    private final ShopRepository shopRepository;
    private final CatalogService catalogService;
    private final Map<String, TenantBotSession> sessions = new ConcurrentHashMap<>();

    public BotRegistryService(ShopRepository shopRepository, CatalogService catalogService) {
        this.shopRepository = shopRepository;
        this.catalogService = catalogService;
    }

    @PostConstruct
    public void initialize() {
        shopRepository.findByActiveTrueAndStatus(ShopStatus.ACTIVE)
                .forEach(this::registerShopBot);
    }

    public void registerShopBot(Shop shop) {
        if (sessions.containsKey(shop.getBotUsername())) {
            return;
        }

        TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
        OkHttpTelegramClient client = new OkHttpTelegramClient(shop.getBotToken());

        LongPollingUpdateConsumer consumer = updates -> updates.forEach(update -> handleUpdate(shop, client, update));
        try {
            app.registerBot(shop.getBotToken(), consumer);
            sessions.put(shop.getBotUsername(), new TenantBotSession(shop.getId(), app));
        } catch (Exception ignored) {
        }
    }

    private void handleUpdate(Shop shop, OkHttpTelegramClient client, Update update) {
        if (update.getMessage() == null || update.getMessage().getChatId() == null) {
            return;
        }

        String text;
        if (shop.getExpiresAt().isBefore(OffsetDateTime.now()) || shop.getStatus() == ShopStatus.EXPIRED) {
            text = "To'lov kutilmoqda. Do'kon vaqtincha to'xtatilgan.";
        } else {
            long count = catalogService.getProducts(shop.getId()).size();
            text = "Shop: @" + shop.getBotUsername() + "\nFaol mahsulotlar soni: " + count;
        }

        try {
            client.execute(SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(text)
                    .build());
        } catch (Exception ignored) {
        }
    }

    private record TenantBotSession(java.util.UUID shopId, TelegramBotsLongPollingApplication application) {
    }
}
