package uk.co.asepstrath.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessTest {

    Business business;

    @BeforeEach
    public void setUp(){
        business = new Business("id", "name", "category", "sanctioned");
    }

    @Test
    void getId() {
        assertEquals(business.getId(), "id");
    }

    @Test
    void getName() {
        assertEquals(business.getName(), "name");
    }

    @Test
    void getCategory() {
        assertEquals(business.getCategory(), "category");
    }

    @Test
    void getSanctioned() {
        assertEquals(business.getSanctioned(), "sanctioned");
    }

    @Test
    void setId() {
        business.setId("newId");
        assertEquals("newId", business.getId());
    }

    @Test
    void setName() {
        business.setName("newName");
        assertEquals("newName", business.getName());
    }

    @Test
    void setCategory() {
        business.setCategory("newCategory");
        assertEquals("newCategory", business.getCategory());
    }

    @Test
    void setSanctioned() {
        business.setSanctioned("newSanction");
        assertEquals("newSanction", business.getSanctioned());
    }
}