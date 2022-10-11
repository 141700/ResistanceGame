//package resistanceGame.service;
//
//import resistanceGame.exception.LocationNotFoundException;
//import resistanceGame.model.User;
//import resistanceGame.repository.UserJpaRepository;
//import lombok.NoArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@NoArgsConstructor
//@Component
//@Log4j2
//public class UserService {
//
//    @Autowired
//    UserJpaRepository repository;
//
//    @Transactional
//    @Cacheable(value = "userCache")
//    public List<User> findAll() {
//        try (Stream<User> stream = repository.readAllByIdNotNull()) {
//            return stream.collect(Collectors.toList());
//        }
//    }
////
////    @CacheEvict(value = "userCache")
////    public HashMap<Integer, User> findByIdAndUpdatePopularity(int[] ids) {
////        List<Long> list = Arrays.stream(ids)
////                .mapToLong(i -> i)
////                .boxed()
////                .collect(Collectors.toList());
////        List<Location> locations = repository.findAllById(list);
////        updatePopularity(locations);
////        return locations.stream()
////                .collect(Collectors.toMap(entry -> Math.toIntExact(entry.getId()),
////                        entry -> entry,
////                        (prev, next) -> next,
////                        HashMap::new));
////    }
//
//    public User findById(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new LocationNotFoundException(id));
//    }
//
//}
//
