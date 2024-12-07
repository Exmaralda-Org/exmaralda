/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.orthonormal.lexicon;

import java.util.Objects;

/**
 *
 * @author bernd
 */
public class FormLanguagePair implements Comparable<FormLanguagePair> {
    
    public String form;
    public String language;

    public FormLanguagePair(String form, String language) {
        this.form = form;
        this.language = language;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FormLanguagePair other = (FormLanguagePair) obj;
        if (!Objects.equals(this.form, other.form)) {
            return false;
        }
        return Objects.equals(this.language, other.language);
    }


    @Override
    public int compareTo(FormLanguagePair o) {
        return this.form.compareTo(o.form);
    }

    
    
    
}
