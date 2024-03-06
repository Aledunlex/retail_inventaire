package fr.sra1.referencement.controllers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractController {
    protected static final String ORDER_ATTRIBUTE_NAME = "order";

    protected static final String MESSAGE_ATTRIBUTE_NAME = "message";

    protected static final String ARTICLE_ATTRIBUTE_NAME = "article";

    protected static final String ARTICLES_ATTRIBUTE_NAME = "articles";

    protected static final String EXISTING_ARTICLE_ATTRIBUTE_NAME = "savedArticle";

    protected static final String REFERENCE_ATTRIBUTE_NAME = "reference";

    protected static final String CATEGORIES_ATTRIBUTE_NAME = "categories";

    protected static final String STOCK_ATTRIBUTE_NAME = "stock";

    protected  static final String EXISTING_STOCK_ATTRIBUTE_NAME = "savedStock";

    protected static final String STOCKS_ATTRIBUTE_NAME = "stocks";

    protected static final String STOCK_WRAPPER_ATTRIBUTE_NAME = "stockWrapper";

    protected static final String CURRENT_PAGE_ATTRIBUTE_NAME = "currentPage";

    protected static final String TOTAL_PAGES_ATTRIBUTE_NAME = "totalPages";

    protected static final String TOTAL_ITEMS_ATTRIBUTE_NAME = "totalItems";

    protected static final String ERROR_ATTRIBUTE_NAME = "error";
}
