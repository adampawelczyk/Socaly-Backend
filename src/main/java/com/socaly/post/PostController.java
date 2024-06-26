package com.socaly.post;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Long> createPost(@RequestBody PostRequest postRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(postRequest));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PostResponse> get(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findPostById(id));
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<Long> edit(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.edit(id, postRequest));
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<PostResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAll());
    }

    @GetMapping("/get/all/by-user/{name}")
    public ResponseEntity<List<PostResponse>> getAllByUser(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllByUser(name));
    }

    @GetMapping("/get/all/by-community/{name}")
    public ResponseEntity<List<PostResponse>> getAllByCommunity(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllByCommunity(name));
    }
}
