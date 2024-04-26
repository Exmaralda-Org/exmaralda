/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.common.corpusbuild;

import java.util.Objects;

/**
 *
 * @author bernd
 */
public class CategoryPlusType {
    
    public String category;
    public String type;

    public CategoryPlusType(String category, String type) {
        this.category = category;
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.category);
        hash = 11 * hash + Objects.hashCode(this.type);
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
        final CategoryPlusType other = (CategoryPlusType) obj;
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        return Objects.equals(this.type, other.type);
    }
    
}
