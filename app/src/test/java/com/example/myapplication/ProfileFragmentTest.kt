package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class ProfileFragmentTest {
    private var profileFragment: ProfileFragment? = null

    @Before
    fun setUp() {
        profileFragment = ProfileFragment()
    }

    @Test
    fun testGetEmail() {

        //Test getEmail function
        val emailVal = "brennan8192@gmail.com"
        val follwerCountVal = 12
        val displayNameVal = "Brennan"
        val testProfile = ProfileFragment.UserProfile(displayNameVal,emailVal,follwerCountVal,"");
        assertEquals(emailVal,profileFragment!!.getEmail(testProfile));

    }

    @Test
    fun testGetDisplayName() {

        //Test getEmail function
        val emailVal = "brennan8192@gmail.com"
        val follwerCountVal = 12
        val displayNameVal = "Brennan"
        val testProfile = ProfileFragment.UserProfile(displayNameVal,emailVal,follwerCountVal,"");
        assertEquals(displayNameVal,profileFragment!!.getDisplayName(testProfile));

    }

    @Test
    fun testGetFollowerCount() {

        //Test getEmail function
        val emailVal = "brennan8192@gmail.com"
        val follwerCountVal = 12
        val displayNameVal = "Brennan"
        val testProfile = ProfileFragment.UserProfile(displayNameVal,emailVal,follwerCountVal,"");
        assertEquals(follwerCountVal,profileFragment!!.getFollowers(testProfile));

    }
}