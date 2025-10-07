-- Place your queries here. Docs available https://www.hugsql.org/
-- :name insert-slideshow :returning-execute
insert into slideshow (slideshow_name)
values (:slideshow-name)
returning slideshow_id;

-- :name get-slideshows :query
select * from slideshow;

-- :name slideshow-name :execute
update slideshow
set slideshow_name = :slideshow_name
where slideshow_id = :slideshow_id

-- :name slideshow-details :execute
update slideshow
set details = :details
where slideshow_id = :slideshow_id
