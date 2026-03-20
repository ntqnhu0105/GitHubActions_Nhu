/* Extracted script */

        window.dataLayer = window.dataLayer || [];

        dataLayer.push({
            pageType: 'Home',
            pagePlatform: 'Web',
            pageStatus: 'Kinh doanh',
            user: {
                userId: '',
                loggedIn: false
            }
        });

    

/* Extracted script */

        (function (w, d, s, l, i) {
            w[l] = w[l] || []; w[l].push({
                'gtm.start':
                    new Date().getTime(), event: 'gtm.js'
            }); var f = d.getElementsByTagName(s)[0],
                j = d.createElement(s), dl = l != 'dataLayer' ? '&l=' + l : ''; j.async = true; j.src =
                    'https://www.googletagmanager.com/gtm.js?id=' + i + dl; f.parentNode.insertBefore(j, f);
        })(window, document, 'script', 'dataLayer', 'GTM-WKQZL8');
    

/* Extracted script */

                        const CookieCustomerLocation = () => {
                            let nameCookie = 'DMX_Personal';
                            if (isStaging) nameCookie += '_Staging';
                            if (isBeta) nameCookie += '_Beta';
                            return nameCookie;
                        }
                        function initFadeRemindLocation() {
                            var remindElems = document.querySelectorAll('.remind_location');
                            remindElems.forEach(function (elem) {
                                elem.classList.remove('hide');
                            });
                            const remind = document.querySelector('.remind_location');
                            if (remind) {
                                setTimeout(() => {
                                    remind.classList.add('fade-out');

                                    setTimeout(() => {
                                        remind.style.display = 'none';
                                    }, 2000);
                                }, 10000);
                            }
                        }

                        function hasLocationTwoLevel(cus) {
                            return cus && cus.ProvinceId > 0 && cus.WardId > 0;
                        }

                        function inintRemindLocation() {
                            var remindElems = document.querySelectorAll('.remind_location');

                            if (remindElems.length === 0) return;

                            var locationCookie = getCookieV2(CookieCustomerLocation());

                            if (locationCookie) {
                                var customer = null;
                                try {
                                    customer = JSON.parse(locationCookie);
                                } catch (e) {
                                    console.warn("Cookie location không hợp lệ:", locationCookie, e);
                                }

                                if (!hasLocationTwoLevel(customer)) {
                                    if (getCookieV2("offRemindLocation") !== "1") {
                                        initFadeRemindLocation();
                                    }
                                } else {
                                    return;
                                }
                            }
                            if (getCookieV2(GetNamCookiesRunlive(cookieHis)) === '') {
                                if (getCookieV2("offRemindLocation") !== "1") {
                                    initFadeRemindLocation();
                                }
                                return;
                            } else {
                                $.ajax({
                                    url: `${lsmhPath}`,
                                    type: 'GET',
                                    success: function (res) {
                                        // Có cookie nhưng hết hạn hoặc không hợp lệ => chưa đăng nhập
                                        if (res.statusCode === 400) {
                                            if (getCookieV2("offRemindLocation") !== "1") {
                                                initFadeRemindLocation();
                                            }
                                        }
                                    },
                                    error: function (xhr) {
                                        console.error(xhr);
                                    }
                                });
                                return;
                            }
                        }


                        document.querySelector('.remind_location').addEventListener('click', (e) => {
                            e.stopPropagation();
                        });
                        document.querySelector('.remind_change').addEventListener('click', (e) => {
                            e.stopPropagation();
                            OpenLocation();
                        });
                        document.querySelector('.remind_btn-close').addEventListener('click', (e) => {
                            e.stopPropagation();
                            $('.remind_location').addClass('hide');
                            setCookie("offRemindLocation", 1, 30);
                        });
                    

/* Extracted script */

        //if ($('#auto-theme').length > 0) {
        //    $('body').addClass($('#auto-theme').val());
        //    if ($('#auto-left-top').length > 0) {
        //        $('header').prepend('<img src=' + $('#auto-left-top').val() + ' class=' + 'header-left' + '>');
        //    }
        //    if ($('#auto-right-top').length > 0) {
        //        $('header').append('<img src=' + $('#auto-right-top').val() + ' class=' + 'header-right' + '>');
        //    }
        //}

        // jQuery isn't loaded
        var autoThemeElement = document.getElementById('auto-theme');
        if (autoThemeElement) {
            // Add the value of 'auto-theme' as a class to the body
            var themeClasses = autoThemeElement.value.split(' ');

            // Add each class to the body
            themeClasses.forEach(function (className) {
                document.body.classList.add(className);
            });

            // Check if the element with id 'auto-left-top' exists
            var autoLeftTopElement = document.getElementById('auto-left-top');
            if (autoLeftTopElement) {
                // Prepend an image to the 'header' element with the source from 'auto-left-top'
                var leftImage = document.createElement('img');
                leftImage.src = autoLeftTopElement.value;
                leftImage.className = 'header-left';
                document.querySelector('header').prepend(leftImage);
            }

            // Check if the element with id 'auto-right-top' exists
            var autoRightTopElement = document.getElementById('auto-right-top');
            if (autoRightTopElement) {
                // Append an image to the 'header' element with the source from 'auto-right-top'
                var rightImage = document.createElement('img');
                rightImage.src = autoRightTopElement.value;
                rightImage.className = 'header-right';
                document.querySelector('header').append(rightImage);
            }
        }
    

/* Extracted script */

        var rooturl = '.thegioididong.com';


    

/* Extracted script */

        document.query = { siteId: 1 };
        document.cusIdKey = "_customerId";
        document.isStickyHeader = true;
        $(document).ready(function () {
            if (typeof loadBannerPMHSticky === "function") {
                loadBannerPMHSticky(true, 0);
            }
        });
    

