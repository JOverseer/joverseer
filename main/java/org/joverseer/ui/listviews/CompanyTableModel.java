package org.joverseer.ui.listviews;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Company;
import org.springframework.context.MessageSource;


public class CompanyTableModel extends ItemTableModel {
    public CompanyTableModel(MessageSource messageSource) {
        super(Company.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"hexNo", "commander", "memberStr"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class};
    }

}
