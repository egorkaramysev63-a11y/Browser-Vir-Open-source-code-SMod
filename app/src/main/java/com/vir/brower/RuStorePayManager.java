package com.vir.brower;

import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import ru.rustore.sdk.billingclient.RuStoreBillingClient;
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory;

public class RuStorePayManager {

    private final Context context;
    private RuStoreBillingClient billingClient;

    public static final List<String> FREE_ITEMS = new ArrayList<>();
    public static final List<String> VIP_PRODUCT_IDS = new ArrayList<>();
    public static final List<String> DIAMOND_PRODUCT_IDS = new ArrayList<>();

    static {
        for (int i = 1; i <= 10; i++) FREE_ITEMS.add("free_item_id_" + i);
        for (int i = 1; i <= 10; i++) VIP_PRODUCT_IDS.add("vip_product_id_" + i);
        for (int i = 1; i <= 10; i++) DIAMOND_PRODUCT_IDS.add("diamonds_pack_id_" + i);
    }

    public RuStorePayManager(Context context) {
        this.context = context;
        initRuStoreSDK();
    }

    private void initRuStoreSDK() {
        // Создание клиента по спецификации RuStore SDK 6.x
        billingClient = RuStoreBillingClientFactory.INSTANCE.create(
                context,
                "123456", // Вставьте сюда ваш Console Application ID из консоли RuStore
                "ru.vk.store",
                null
        );
    }

    /**
     * Покупка товара (VIP или Алмазы) через RuStore SDK 6.x
     */
    /**
     * Покупка товара (VIP или Алмазы) через RuStore SDK 6.x
     */
    public void purchaseProduct(final String productId, final String currentUserId, final int currentDiamonds) {
        List<String> productIds = Collections.singletonList(productId);

        billingClient.getProducts().getProducts(productIds)
                .addOnSuccessListener(products -> {
                    if (products != null && !products.isEmpty()) {
                        // Генерируем уникальный идентификатор транзакции
                        String uniqueOrderId = UUID.randomUUID().toString();

                        // ИСПРАВЛЕНО: Явное приведение null к типу String, чтобы Java-компилятор распознал перегрузку метода Kotlin
                        billingClient.getPurchases().purchaseProduct(
                                        productId,
                                        uniqueOrderId,
                                        1,
                                        (String) null
                                )
                                .addOnSuccessListener(paymentResult -> {
                                    handleSuccessfulPurchase(productId, currentUserId, currentDiamonds);
                                })
                                .addOnFailureListener(throwable -> {
                                    android.util.Log.e("RUSTORE_PAY", "Ошибка платежа: " + throwable.getMessage());
                                });
                    }
                })
                .addOnFailureListener(throwable -> {
                    android.util.Log.e("RUSTORE_PAY", "Ошибка загрузки продуктов: " + throwable.getMessage());
                });
    }

    private void handleSuccessfulPurchase(String productId, String userId, int currentDiamonds) {
        if (VIP_PRODUCT_IDS.contains(productId)) {
            // Передаем 11 параметров в обновленный VirAccountManager (включая флаг VK = false по умолчанию)
            VirAccountManager.saveAccountData(userId, "Профиль", "01.01.2026", "pass", "Семья", false, true, true, true, currentDiamonds, false);
        } else if (DIAMOND_PRODUCT_IDS.contains(productId)) {
            int addedDiamonds = 10;
            try {
                String indexStr = productId.substring(productId.lastIndexOf("_") + 1);
                addedDiamonds = Integer.parseInt(indexStr) * 10;
            } catch (Exception e) {
                // Игнорируем ошибку парсинга индекса
            }
            int newTotal = currentDiamonds + addedDiamonds;
            // Передаем 11 параметров в обновленный VirAccountManager (включая флаг VK = false по умолчанию)
            VirAccountManager.saveAccountData(userId, "Профиль", "01.01.2026", "pass", "Семья", false, true, true, false, newTotal, false);
        }
    }
}
