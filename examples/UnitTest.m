- (void)testGetObject
{
    PFQuery *query = [PFQuery queryWithClassName:@"github"];
    [query getObjectInBackgroundWithId:@"55d6c9cd03646c25b6ad9d5e" block:^(PFObject *object, NSError *error) {
        if (error) {
            NSLog(@"get error: %@", error);
        } else {
            NSLog(@"get OK");
            NSLog(@"%@", object);
        }
    }];
}

- (void)testQuery
{
    PFQuery *query = [PFQuery queryWithClassName:@"github"];
    [query whereKey:@"name" equalTo:@"doremi"];
    [query findObjectsInBackgroundWithBlock:^(NSArray *objs, NSError *error) {
        if (error) {
            NSLog(@"query error: %@", error);
        } else {
            [objs enumerateObjectsUsingBlock:^(PFObject *obj, NSUInteger idx, BOOL *stop) {
                NSLog(@"name: %@", obj[@"name"]);
                NSLog(@"age:  %d", [obj[@"age"] intValue]);
            }];
        }
    }];
}

- (void)testComplexQuery
{
    PFQuery *query = [PFQuery queryWithClassName:@"github"];
    [query orderByDescending:@"name"];
    [query whereKey:@"age" lessThan:@20];
    [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
        if (error) {
            NSLog(@"complex query error: %@", error);
        } else {
            [objects enumerateObjectsUsingBlock:^(PFObject *obj, NSUInteger idx, BOOL *stop) {
                NSLog(@"%lu, %@, %lu", idx, obj[@"name"], [obj[@"age"] integerValue]);
            }];
        }
    }];
}

- (void)testRelation
{
    PFObject *myPost = [PFObject objectWithClassName:@"Post"];
    myPost[@"title"] = @"I'm Hungry";
    myPost[@"content"] = @"Where should we go for lunch?";

    // Create the comment
    PFObject *myComment = [PFObject objectWithClassName:@"Comment"];
    myComment[@"content"] = @"Let's do Sushirrito.";

    // Add a relation between the Post and Comment
    myComment[@"parent"] = myPost;

    // This will save both myPost and myComment
    [myComment saveInBackgroundWithBlock:^(BOOL succ, NSError *error) {
        if (succ) {
            NSLog(@"save relation OK");
        } else {
            NSLog(@"save error: %@", error);
        }
    }];
}

- (void)testPin
{
    PFObject *obj = [PFObject objectWithClassName:@"github"];
    obj[@"name"] = @"doremi";
    obj[@"age"] = @18;
    [obj pinInBackgroundWithBlock:^(BOOL succ, NSError *error) {
        if (succ) {
            NSLog(@"pin OK");
            NSLog(@"%@", obj);
        } else {
            NSLog(@"pin error: %@", error);
        }
    }];
}


- (void)testFetchLocal
{
    PFQuery *query = [PFQuery queryWithClassName:@"github"];
    [query fromLocalDatastore];
    [query getFirstObjectInBackgroundWithBlock:^(PFObject *result, NSError *error) {
        if (error) {
            NSLog(@"fetch local error: %@", error);
        } else {
            NSLog(@"fetch OK");
            NSLog(@"%@", result);
        }
    }];
}

- (void)testUnPin
{
    [PFObject unpinAllObjectsInBackgroundWithBlock:^(BOOL succ, NSError *error) {
        if (succ) {
            NSLog(@"unpin all OK");
        } else {
            NSLog(@"unpin all error: %@", error);
        }
    }];
}

- (void)testCreate
{
    PFObject *obj = [PFObject objectWithClassName:@"github"];
    obj[@"name"] = @"doremi";
    obj[@"age"] = @18;
    [obj saveInBackgroundWithBlock:^(BOOL succ, NSError *error) {
        if (succ) {
            NSLog(@"save OK");
            NSLog(@"objectId: %@", obj.objectId);
        } else {
            NSLog(@"save error: %@", error);
        }
    }];
}
