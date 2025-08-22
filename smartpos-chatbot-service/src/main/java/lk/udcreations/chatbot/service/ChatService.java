package lk.udcreations.chatbot.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import lk.udcreations.chatbot.config.ProductServiceClient;
import lk.udcreations.common.dto.product.ProductDTO;

@Service
public class ChatService {
	
	private final OpenAiService openAiService;
	private final ProductServiceClient productServiceClient;

	public ChatService(@Value("${openai.api.key}") String apiKey, ProductServiceClient productServiceClient) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
        this.productServiceClient = productServiceClient;
        
    }

	
	public String getReply(String message, String sessionId) {
        // Step 1: Ask GPT what to do
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(
                        new ChatMessage("system", "You are a POS assistant. If user asks about product stock, say: CALL getInventory('<name>'). Otherwise, answer normally."),
                        new ChatMessage("user", message)
                ))
                .maxTokens(200)
                .build();

        String draft = openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();

        // Step 2: If GPT asks to CALL getInventory
        if (draft.startsWith("CALL getInventory(")) {
            String productQuery = draft.substring("CALL getInventory(".length(), draft.length()-1).replace("'", "");
            String result = getInventory(productQuery);

            // Step 3: Ask GPT to rephrase into natural answer
            ChatCompletionRequest followUp = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(List.of(
                            new ChatMessage("system", "Answer clearly with product and stock count."),
                            new ChatMessage("user", "Inventory lookup result: " + result)
                    ))
                    .maxTokens(100)
                    .build();

            return openAiService.createChatCompletion(followUp)
                    .getChoices().get(0).getMessage().getContent();
        }

        return draft;
    }
	
	public String getInventory(String skuOrName) {
        // First try by SKU
        ProductDTO bySku = productServiceClient.findBySku(skuOrName);
        if (bySku != null) {
            return buildResponse(bySku);
        }

        // Else try by product name
        List<ProductDTO> products = productServiceClient.searchProductsByName(skuOrName);
        if (!products.isEmpty()) {
            return buildResponse(products.get(0)); // for now, just return first match
        }

        return "No product found for: " + skuOrName;
    }

    private String buildResponse(ProductDTO product) {
        return productServiceClient.getInventoryByProductId(product.getId()).toString();
                //.map(inv -> product.getProductName() + " → " + inv.getQuantity() + " units")
                //.orElse(product.getProductName() + " → no stock info");
    }
	
}
