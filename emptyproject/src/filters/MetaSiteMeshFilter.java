package filters;

import java.io.IOException;

import org.sitemesh.DecoratorSelector;
import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.content.Content;
import org.sitemesh.webapp.WebAppContext;

/**
 * Servlet Filter implementation class RorSiteMeshFilter
 */
public class MetaSiteMeshFilter extends ConfigurableSiteMeshFilter {

	private final String DECORATOR_PREFIX = "/WEB-INF/templates/";
	private final String DECORATOR_SUFFIX = ".jsp";

	@Override
	protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
		super.applyCustomConfiguration(builder);
		builder.setCustomDecoratorSelector(new DecoratorSelector<WebAppContext>() {

			@Override
			public String[] selectDecoratorPaths(Content content,
					WebAppContext context) throws IOException {
				String decoratorPath = content.getExtractedProperties()
						.getChild("meta").getChild("layout").getValue();
				if (decoratorPath == null || decoratorPath.trim().equals(""))
					return new String[] {};
				else
					return new String[] { DECORATOR_PREFIX + decoratorPath.trim()
							+ DECORATOR_SUFFIX };
			}
		});
	}

}
