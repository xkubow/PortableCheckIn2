package cz.tsystems.base;


import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy;

public class LowerCaseNamingStrategy extends LowerCaseWithUnderscoresStrategy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    @Override
    public String translate(String arg0) {
        return arg0.toUpperCase();
    }
    
}
