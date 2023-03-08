package searchengine.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    @Transactional
    @Query("select p from Page p where p.site.id = ?1")
    Optional<List<Page>> findAllBySiteId(Integer id);

    @Transactional
    @Modifying
    @Query("delete from Page p where p.site.id = ?1")
    Integer deleteAllBySiteId(Integer id);
}
