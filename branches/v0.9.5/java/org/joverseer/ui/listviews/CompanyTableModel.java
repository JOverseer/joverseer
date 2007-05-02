package org.joverseer.ui.listviews;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Company;
import org.joverseer.ui.domain.CompanyWrapper;
import org.springframework.context.MessageSource;


public class CompanyTableModel extends ItemTableModel {
    public CompanyTableModel(MessageSource messageSource) {
        super(CompanyWrapper.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"hexNo", "nationNo", "commander", "memberStr"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class};
    }

}
