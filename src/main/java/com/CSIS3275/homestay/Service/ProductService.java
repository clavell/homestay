//package com.CSIS3275.homestay.Service;
//
//import com.CSIS3275.homestay.Entity.User;
//import com.CSIS3275.homestay.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ProductService {
//    @Autowired
//    private UserRepository repository;
//
////    public User saveProduct(User user){
////        return repository.save(user);
////    }
////
////    public List<User> saveProducts(List<User> users){
////        return repository.saveAll(users);
////    }
////
////    public List<User> getProducts(){
////        return repository.findAll();
////    }
////
////    public User getProductbyID(int id){
////        return repository.findById(id).orElse(null);
////    }
////
////    public List<User> getProductbyName(String name){
////        return repository.findByName(name);
////    }
////
////    public String deleteProductbyID(int id){
////        repository.deleteById(id);
////        return "Product removed for id - " + id;
////    }
//
//    public User updateProduct(User user){
//        User existingUser = repository.findById(user.getId()).orElse(null);
//        existingUser.setName(user.getName());
////        existingUser.setQuantity(user.getQuantity());
////        existingUser.setPrice(user.getPrice());
//        return repository.save(existingUser);
//    }
//
//}
