(ns simpleui.flashcards3.web.views.icons)

[:div.h-6.w-6]
(defn- hero-icon [width & paths]
  [:xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/svg
   {:fill "none",
    :viewBox "0 0 24 24",
    :stroke-width "1.5",
    :stroke "currentColor",
    :class (format "h-%s w-%s" width width)}
   (for [d paths]
     [:xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/path
      {:stroke-linecap "round",
       :stroke-linejoin "round",
       :d d}])])

(defmacro deficon [sym & paths]
  `(do
    (def ~sym (hero-icon 6 ~@paths))
    (defn ~(symbol (str sym "-width")) [width#] (hero-icon width# (list ~@paths)))))

(deficon play-circle
  "M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
  "M15.91 11.672a.375.375 0 0 1 0 .656l-5.603 3.113a.375.375 0 0 1-.557-.328V8.887c0-.286.307-.466.557-.327l5.603 3.112Z")

(deficon arrow-up
  "M4.5 10.5 12 3m0 0 7.5 7.5M12 3v18")
(deficon arrow-down
  "M19.5 13.5 12 21m0 0-7.5-7.5M12 21V3")

(deficon home
  "m2.25 12 8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25")
