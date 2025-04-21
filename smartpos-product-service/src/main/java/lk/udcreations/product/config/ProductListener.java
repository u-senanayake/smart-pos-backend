package lk.udcreations.product.config;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Query;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.entity.Product;
import lk.udcreations.product.repository.CategoryRepository;

@Component
public class ProductListener {

	private CategoryRepository categoryRepository;

	private EntityManager entityManager;

	public ProductListener(@Lazy CategoryRepository categoryRepository, @Lazy EntityManager entityManager) {
		super();
		this.categoryRepository = categoryRepository;
		this.entityManager = entityManager;
	}

	@PrePersist
	public void beforeProductInsert(Product product) {
		if (product.getProductId() == null) {
			String categoryPrefix = categoryRepository.findById(product.getCategoryId())
					.map(Category::getCatPrefix)
					.orElse("G"); // Default prefix if category is not found

			// Generate sequence dynamically in H2 (Use application logic instead of
			// sequences)
			Long nextSeqValue = getNextProductSequence(product.getCategoryId());
			product.setProductId(categoryPrefix + String.format("%05d", nextSeqValue));
		}
	}

	private Long getNextProductSequence(Integer categoryId) {
		Query query = entityManager.createNativeQuery(
				"SELECT COALESCE(MAX(CAST(SUBSTRING(product_id, 4, 5) AS INT)), 0) + 1 FROM Product WHERE category_id = :categoryId");
		query.setParameter("categoryId", categoryId);
		return ((Number) query.getSingleResult()).longValue();
	}
}
