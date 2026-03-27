package com.fellow.app.model;

/**
 * Course (Ders) sınıfı - Bir dersin tüm bilgilerini tutar
 * 
 * @author Fellow Team
 * @version 1.0
 */
public class Course {
    
    // ========== ÖZELLİKLER (Fields) ==========
    
    private int id;              // Veritabanındaki benzersiz kimlik numarası
    private String courseName;   // Ders adı (örn: "Yazılım Mühendisliği")
    private String colorHex;     // Ders rengi (örn: "#6366f1")
    private int userId;          // Bu dersi ekleyen kullanıcının ID'si
    
    // ========== CONSTRUCTOR (Yapıcı Metotlar) ==========
    
    /**
     * Boş constructor - Veritabanından veri çekerken kullanılır
     */
    public Course() {
    }
    
    /**
     * Tam constructor - Yeni ders oluştururken kullanılır
     * 
     * @param courseName Ders adı
     * @param colorHex   Ders rengi (hex kodu, örn: "#FF5733")
     * @param userId     Kullanıcı ID'si
     */
    public Course(String courseName, String colorHex, int userId) {
        this.courseName = courseName;
        this.colorHex = colorHex;
        this.userId = userId;
    }
    
    /**
     * ID'li constructor - Veritabanından çekerken kullanılır
     * 
     * @param id         Ders ID'si
     * @param courseName Ders adı
     * @param colorHex   Ders rengi
     * @param userId     Kullanıcı ID'si
     */
    public Course(int id, String courseName, String colorHex, int userId) {
        this.id = id;
        this.courseName = courseName;
        this.colorHex = colorHex;
        this.userId = userId;
    }
    
    // ========== GETTER METOTLARI (Veriyi Okuma) ==========
    
    /**
     * Ders ID'sini döndürür
     * @return id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Ders adını döndürür
     * @return courseName
     */
    public String getCourseName() {
        return courseName;
    }
    
    /**
     * Ders rengini döndürür
     * @return colorHex
     */
    public String getColorHex() {
        return colorHex;
    }
    
    /**
     * Kullanıcı ID'sini döndürür
     * @return userId
     */
    public int getUserId() {
        return userId;
    }
    
    // ========== SETTER METOTLARI (Veriyi Değiştirme) ==========
    
    /**
     * Ders ID'sini ayarlar
     * @param id Yeni ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Ders adını ayarlar
     * @param courseName Yeni ders adı
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    /**
     * Ders rengini ayarlar
     * @param colorHex Yeni renk kodu (örn: "#FF5733")
     */
    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }
    
    /**
     * Kullanıcı ID'sini ayarlar
     * @param userId Yeni kullanıcı ID'si
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    // ========== YARDIMCI METOTLAR ==========
    
    /**
     * Dersi string olarak gösterir (debugging için)
     * @return Ders bilgisi
     */
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", colorHex='" + colorHex + '\'' +
                ", userId=" + userId +
                '}';
    }
    
    /**
     * İki dersin eşit olup olmadığını kontrol eder
     * @param obj Karşılaştırılacak nesne
     * @return Eşitse true, değilse false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Course course = (Course) obj;
        return id == course.id;
    }
    
    /**
     * Hash kodu üretir (HashMap, HashSet için gerekli)
     * @return Hash kodu
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}