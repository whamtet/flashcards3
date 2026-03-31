-- Place your queries here. Docs available https://www.hugsql.org/
-- :name insert-slideshow :returning-execute
insert into slideshow (slideshow_name)
values (:slideshow_name)
returning slideshow_id;

-- :name get-slideshows :query
select * from slideshow;
-- :name get-slideshow :query :one
select * from slideshow where slideshow_id = :slideshow_id
-- :name get-slideshow-name :query :one
select * from slideshow where slideshow_name = :slideshow_name

-- :name slideshow-name :execute
update slideshow
set slideshow_name = :slideshow_name
where slideshow_id = :slideshow_id

-- :name slideshow-details :execute
update slideshow
set details = :details
where slideshow_id = :slideshow_id

-- :name slideshow-delete :execute
delete from slideshow where slideshow_id = :slideshow_id
